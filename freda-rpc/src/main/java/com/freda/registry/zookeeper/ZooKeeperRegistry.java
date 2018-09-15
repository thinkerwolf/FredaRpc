package com.freda.registry.zookeeper;

import com.freda.common.Net;
import com.freda.common.util.RandomUtil;
import com.freda.registry.AbstractRegistry;
import com.freda.registry.Server;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    /**
     * 服务列表 <protocol, <serverName, server>>
     */
    private Map<String, Server> serverMap = new ConcurrentHashMap<String, Server>();

    public ZooKeeperRegistry(Net net) {
        super(net);
    }

    @Override
    public void start() throws Exception {
        latch = new CountDownLatch(1);
        String connectAddress = net.getHost() + ":" + net.getPort();
        zk = new ZooKeeper(connectAddress, net.getTimeout(), this);
        latch.await();
        recursiveSafeCreate(DEFAULT_ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, false);
        setConnected(true);
        fatchServer();
        logger.debug("ZooKeeper client listen on " + connectAddress + " success!");
        mySpecialLogger.info("myspecial zookeeper registry success");
    }

    @Override
    public void register(Server server) throws Exception {
        if (null == server) {
            throw new IllegalArgumentException("server can't be null");
        }
        String path = DEFAULT_ROOT_PATH + "/" + server.getName();
        recursiveSafeCreate(path, server.toJsonByte(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, false);
        fatchToServerMap(server);
    }

    private void fatchToServerMap(Server server) {
        serverMap.put(server.getName(), server);
    }

    private void recursiveSafeCreate(String path, byte[] data, List<ACL> acls, CreateMode mode, boolean exitDelete)
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
        if (stat == null) {
            zk.create(path, data, acls, mode);
        } else {
            if (exitDelete) {
                zk.delete(path, -1);
                zk.create(path, data, acls, mode);
            }
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
        serverMap.remove(server.getName());
    }

    @Override
    public Server getRandomServer(String protocol) throws Exception {
        List<Server> servers = getServersByProtocol(protocol);
        int s = servers.size();
        if (s == 0) {
            return null;
        } else if (s == 1) {
            return servers.get(0);
        } else {
            return servers.get(RandomUtil.nextInt(s));
        }
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
        List<String> serverNames = zk.getChildren(DEFAULT_ROOT_PATH, false);
        if (serverNames != null && serverNames.size() > 0) {
            for (String serverName : serverNames) {
                byte[] bytes = zk.getData(DEFAULT_ROOT_PATH + "/" + serverName, true, null);
                if (bytes != null && bytes.length > 0) {
                    fatchToServerMap(Server.jsonToServer(new String(bytes)));
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
            if (path.indexOf(DEFAULT_ROOT_PATH + "/") < 0) {
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
                byte[] bytes = zk.getData(path, true, null);
                fatchToServerMap(Server.jsonToServer(new String(bytes)));
                break;
            }
            case NodeDeleted: {
                // Node被删除
//			String name = path.substring(path.lastIndexOf("/") + 1, path.length());
//			serverMap.remove(name);
//			mySpecialLogger.info("NodeDeleted " + path);
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
                start();
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

    @Override
    public List<Server> getServersByProtocol(String protocol) {
        List<Server> list = new ArrayList<>();
        for (Server server : serverMap.values()) {
            if (protocol.equals(server.getProtocol()))
                list.add(server);
        }
        return list;
    }

}
