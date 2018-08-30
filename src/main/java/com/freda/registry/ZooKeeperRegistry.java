package com.freda.registry;

import com.freda.common.conf.RegistryConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author wukai
 */
public class ZooKeeperRegistry extends AbstractRegistry implements Watcher {

	private static final String DEFAULT_ROOT_PATH = "/freda/servers";
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);
	private static final Logger mySpecialLogger = LoggerFactory.getLogger("com.freda.myspecial");

	private ZooKeeper zk;
	private CountDownLatch latch;
	/** 服务列表 <protocol, <serverName, server>> */
	private Map<String, Map<String, Server>> serverMap = new ConcurrentHashMap<String, Map<String, Server>>();

	public ZooKeeperRegistry(RegistryConfig conf) throws Exception {
		super(conf);
		init();
	}

	private void init() throws Exception {
		latch = new CountDownLatch(1);
		zk = new ZooKeeper(conf.getConnAddress(), conf.getTimeout(), this);
		latch.await();
		recursiveSafeCreate(DEFAULT_ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, false);
		setConnected(true);
		fatchServer();
		logger.debug("ZooKeeper client listen on " + conf.getConnAddress() + " success!");
		mySpecialLogger.info("myspecial zookeeper registry success");
	}

	@Override
	public void register(Server server) throws Exception {
		if (null == server) {
			throw new IllegalArgumentException("server can't be null");
		}
		String path = DEFAULT_ROOT_PATH + "/" + server.getProtocal() + "/" + server.getName();
		recursiveSafeCreate(path, server.toJsonByte(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, true);
		fatchToServerMap(server.getProtocal(), server);
	}

	private void fatchToServerMap(String protocol, Server server) {
		Map<String, Server> map = serverMap.get(protocol);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			serverMap.put(protocol, map);
		}
		map.put(server.getName(), server);
	}

	private void recursiveSafeCreate(String path, byte[] date, List<ACL> acls, CreateMode mode, boolean exitDelete)
			throws Exception {
		if (StringUtils.isEmpty(path)) {
			return;
		}
		if ("/".equals(path)) {
			return;
		}
		// 递归创建
		int index = path.lastIndexOf("/");
		if (index < 0) {
			return;
		}
		recursiveSafeCreate(path.substring(0, index), null, acls, CreateMode.PERSISTENT, false);
		Stat stat = zk.exists(path, this);
		// safe delete
		if (exitDelete) {
			if (stat != null) {
				zk.delete(path, -1);
			}
		}
		if (stat == null) {
			Stat s = zk.exists(path, this);
			if (s == null)
				zk.create(path, date, acls, mode);
		}
	}

	@Override
	public void unregister(Server server) throws Exception {
		String path = DEFAULT_ROOT_PATH + "/" + server.getName();
		Stat stat = zk.exists(path, null);
		// safe delete
		if (stat != null) {
			zk.delete(path, -1);
		}
	}

	@Override
	public Server getRandomServer(String protocol) throws Exception {
		Map<String, Server> map = serverMap.get(protocol);
		if (map == null || map.size() == 0) {
			return null;
		}
		return map.values().iterator().next();
	}
	
	public boolean isConnected() {
		States state = zk.getState();
		if (state == States.CLOSED || state == States.AUTH_FAILED) {
			return false;
		}
		return true;
	}

	/**
	 * 拉取服务
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	private void fatchServer() throws KeeperException, InterruptedException {
		List<String> protocols = zk.getChildren(DEFAULT_ROOT_PATH, false);
		if (protocols == null || protocols.size() == 0) {
			return;
		}
		for (String protocol : protocols) {
			List<String> serverNames = zk.getChildren(DEFAULT_ROOT_PATH + "/" + protocol, false);
			if (serverNames != null && serverNames.size() > 0) {
				for (String serverName : serverNames) {
					byte[] bytes = zk.getData(DEFAULT_ROOT_PATH + "/" + protocol + "/" + serverName, true, null);
					fatchToServerMap(protocol, Server.jsonToServer(new String(bytes)));
				}
			}
		}
	}

	public void close() throws Exception {
		zk.close();
	}

	@Override
	public void process(WatchedEvent event) {
		try {
			if (latch.getCount() > 0) {
				latch.countDown();
			}
			if (event.getType() == EventType.None) {
				processSessionEvent(event);
				return;
			}

			String path = event.getPath();
			if (StringUtils.isBlank(path)) {
				return;
			}
			if (path.indexOf(DEFAULT_ROOT_PATH) < 0) {
				return;
			}
			processNodeChange(event, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processNodeChange(WatchedEvent event, String path) throws KeeperException, InterruptedException {
		switch (event.getType()) {
		case NodeCreated: {
			mySpecialLogger.info("NodeCreated");
			break;
		}
		case NodeDeleted: {
			// Node被删除
			mySpecialLogger.info("NodeDeleted " + path);
			break;
		}
		case NodeDataChanged: {
			// 节点数据发生变化 重新拉取数据
			serverMap.clear();
			fatchServer();
			mySpecialLogger.info("NodeDataChanged " + path);
			break;
		}
		case NodeChildrenChanged:
			mySpecialLogger.info("NodeChildrenChanged " + path);
			break;
		default:
			break;

		}
	}

	/**
	 * 处理连接事件
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void processSessionEvent(WatchedEvent event) throws Exception {
		switch (event.getState()) {
		case Expired:
			// 过期了，重新连接，将已注册的serverMap重新注册
			serverMap.clear();
			init();
			break;
		case Disconnected:
			// 断开连接
			setConnected(false);
			break;
		case SyncConnected:
			// 连接上了
			break;
		default:
			break;
		}
	}

}
