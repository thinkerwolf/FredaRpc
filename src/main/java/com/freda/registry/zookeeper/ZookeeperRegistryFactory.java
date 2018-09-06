package com.freda.registry.zookeeper;

import com.freda.common.conf.RegistryConfig;
import com.freda.registry.Registry;
import com.freda.registry.RegistryFactory;

public class ZookeeperRegistryFactory implements RegistryFactory {

	@Override
	public Registry getRegistry(RegistryConfig registryConf) {
		return new ZooKeeperRegistry(registryConf);
	}

}
