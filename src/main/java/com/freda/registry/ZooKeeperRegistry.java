package com.freda.registry;

import com.freda.common.conf.RegistryConfig;
import com.freda.common.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author wukai
 */
public class ZooKeeperRegistry extends AbstractRegistry implements Watcher {

    private static final String DEFAULT_ROOT_PATH = "/freda/servers";
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);
    private static final Random r = new Random();
    private static final Logger mySpecialLogger = LoggerFactory.getLogger("com.freda.myspecial");
    private ZooKeeper zooKeeper;
    private CountDownLatch latch;

    public ZooKeeperRegistry(RegistryConfig conf) throws Exception {
        super(conf);
        latch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(conf.getConnAddress(), conf.getTimeout(), this);
        latch.await();
        recursiveSafeCreate(DEFAULT_ROOT_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, false);
        setConnected(true);
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
            Stat s = zooKeeper.exists(path, null);
            if (s == null)
                zooKeeper.create(path, date, acls, mode);
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
    public Server getRandomServer(String protocal) throws Exception {
        List<String> list = zooKeeper.getChildren(DEFAULT_ROOT_PATH + "/" + protocal, false);
        if (list == null || list.size() == 0) {
            return null;
        }
        String serverPath = list.get(r.nextInt(list.size()));
        byte[] bytes = zooKeeper.getData(DEFAULT_ROOT_PATH + "/" + protocal + "/" + serverPath, true, null);
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

    @Override
    public void process(WatchedEvent event) {
        if (latch.getCount() > 0) {
            latch.countDown();
        }
        KeeperState ks = event.getState();
        if (ks == KeeperState.SyncConnected || ks == KeeperState.ConnectedReadOnly) {
        } else {
            setConnected(false);
        }
        switch (event.getType()) {

            case NodeCreated:
                mySpecialLogger.info("NodeCreated");
                break;
            case NodeDeleted:
                mySpecialLogger.info("NodeDeleted");
                break;
            case NodeChildrenChanged:
                mySpecialLogger.info("NodeChildrenChanged");
                break;
            case NodeDataChanged:
                mySpecialLogger.info("NodeDataChanged");
                break;
            default:
                break;

        }

    }

}
