package com.nettyrpc.remote;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wukai
 *
 */
public class ZooKeeperClient {

	private static final String SERVERS = "/servers";

	private ZooKeeper zooKeeper;

	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperClient.class);

	public ZooKeeperClient(String connStr, int sessionTimeout) throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		zooKeeper = new ZooKeeper(connStr, sessionTimeout, new DefaultWatcher(latch));
		latch.await();
		Stat stat = zooKeeper.exists(SERVERS, new ClientWatcher());
		if (stat == null) {
			zooKeeper.create(SERVERS, "servers".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		logger.info("ZooKeeper client listen on " + connStr + " success!");
	}

	static class DefaultWatcher implements Watcher {
		private CountDownLatch latch;

		public DefaultWatcher(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void process(WatchedEvent event) {
			latch.countDown();
		}

	}

	static class ClientWatcher implements Watcher {

		@Override
		public void process(WatchedEvent event) {
			System.out.println(event);
			if (event.getType() == EventType.NodeCreated) {

			}
			if (event.getType() == EventType.NodeDeleted) {

			}
			if (event.getType() == EventType.NodeDataChanged) {

			}
		}
	}

	/**
	 * 
	 * @param ipStr
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void register(String path, String ipAddress) throws KeeperException, InterruptedException {
		if (StringUtils.isEmpty(ipAddress)) {
			throw new IllegalArgumentException("ipStr can't be null");
		}
		if (!path.startsWith(SERVERS)) {
			path = getPath(path);
		}
		Stat stat = zooKeeper.exists(path, null);
		if (stat == null) {
			zooKeeper.create(path, ipAddress.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		}
	}

	public void unregister(String path) throws InterruptedException, KeeperException {
		if (StringUtils.isEmpty(path)) {
			throw new IllegalArgumentException("ipStr can't be null");
		}
		if (!path.startsWith(SERVERS)) {
			path = getPath(path);
		}
		Stat stat = zooKeeper.exists(path, null);
		if (stat != null) {
			zooKeeper.delete(path, 0);
		}
	}

	private static final Random r = new Random();
	
	/**
	 * 从ZooKeeper注册中心获取可以访问的服务器列表
	 * @return
	 */
	public String getServerAddress() {
		try {
			List<String> list = zooKeeper.getChildren(SERVERS, false);
			if (list == null || list.size() == 0) {
				return null;
			}
			// TODO 服务器选择算法...
			String serverPath = list.get(r.nextInt(list.size()));
			byte[] bytes = zooKeeper.getData(getPath(serverPath), true, null);
			return new String(bytes);
		} catch (Exception e) {
			logger.error("can't get chilren error", e);
			return null;
		}
	}

	public boolean isConnected() {
		States state = zooKeeper.getState();
		if (state == States.CLOSED || state == States.AUTH_FAILED) {
			return false;
		}
		return true;
	}

	public void close() throws InterruptedException {
		zooKeeper.close();
	}

	private String getPath(String ipStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(SERVERS);
		sb.append("/");
		sb.append(ipStr);
		return sb.toString();
	}
	
	
	// TEST code
	public static void main(String[] args) {
		String CONN_STR = "10.8.10.49:2181";
		try {
			ZooKeeperClient zkClient = new ZooKeeperClient(CONN_STR, 1000);
			zkClient.register("server1", "127.0.0.1:8080");
			zkClient.unregister("server1");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
