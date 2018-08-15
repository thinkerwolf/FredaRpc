package com.freda.registry;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freda.util.JsonUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author wukai
 */
public class ZooKeeperRegistry implements Registry {

	private static final String DEFAULT_ROOT_PATH = "/freda/servers";
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);
	private static final Random r = new Random();
	private ZooKeeper zooKeeper;

	public ZooKeeperRegistry(String connStr, int sessionTimeout) throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		zooKeeper = new ZooKeeper(connStr, sessionTimeout, new DefaultWatcher(latch));
		latch.await();
		recursiveSafeCreate(DEFAULT_ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, false);
		logger.debug("ZooKeeper client listen on " + connStr + " success!");
	}

	@Override
	public void register(Server server) throws Exception {
		if (null == server) {
			throw new IllegalArgumentException("server can't be null");
		}
		String path = DEFAULT_ROOT_PATH + "/" + server.getName();
		recursiveSafeCreate(path, server.toJsonByte(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, true);
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
		Stat stat = zooKeeper.exists(path, null);
		// safe delete
		if (exitDelete) {
			if (stat != null) {
				zooKeeper.delete(path, -1);
			}
		}
		if (stat == null) {
			zooKeeper.create(path, date, Ids.OPEN_ACL_UNSAFE, mode);
		}
	}

	@Override
	public void unregister(Server server) throws Exception {
		String path = DEFAULT_ROOT_PATH + "/" + server.getName();
		Stat stat = zooKeeper.exists(path, null);
		// safe delete
		if (stat != null) {
			zooKeeper.delete(path, -1);
		}
	}

	@Override
	public Server getRandomServer() throws Exception {
		List<String> list = zooKeeper.getChildren(DEFAULT_ROOT_PATH, false);
		if (list == null || list.size() == 0) {
			return null;
		}
		String serverPath = list.get(r.nextInt(list.size()));
		byte[] bytes = zooKeeper.getData(DEFAULT_ROOT_PATH + "/" + serverPath, true, null);
		return JsonUtils.json2Obj(new String(bytes), Server.class);
	}

	public boolean isConnected() {
		States state = zooKeeper.getState();
		if (state == States.CLOSED || state == States.AUTH_FAILED) {
			return false;
		}
		return true;
	}

	public void close() throws Exception {
		zooKeeper.close();
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

	// TEST code
	public static void main(String[] args) {
		String CONN_STR = "10.8.10.43:2181";
		try {
			ZooKeeperRegistry zkClient = new ZooKeeperRegistry(CONN_STR, 1000);
			zkClient.register(new Server("freda_1", "127.0.0.1", 8080));
			zkClient.unregister(new Server("freda_1", "127.0.0.1", 8080));
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
