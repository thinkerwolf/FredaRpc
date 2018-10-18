package com.thinkerwolf.freda.registry.zookeeper;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.registry.Registry;
import com.thinkerwolf.freda.registry.RegistryFactory;

public class ZookeeperRegistryFactory implements RegistryFactory {

    @Override
    public Registry getRegistry(Net net) {
        return new ZooKeeperRegistry(net);
    }

}
