package com.freda.registry.zookeeper;

import com.freda.common.Net;
import com.freda.registry.Registry;
import com.freda.registry.RegistryFactory;

public class ZookeeperRegistryFactory implements RegistryFactory {

	@Override
	public Registry getRegistry(Net net) {
		return new ZooKeeperRegistry(net);
	}

}
