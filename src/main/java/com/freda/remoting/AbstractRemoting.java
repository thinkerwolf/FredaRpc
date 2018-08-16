package com.freda.remoting;

import com.freda.common.conf.Configuration;
import com.freda.common.conf.RegistryConfig;
import com.freda.registry.Registry;
import com.freda.registry.ZooKeeperRegistry;

abstract class AbstractRemoting implements Remoting {

	protected Configuration configuration;

	protected Registry registry;

	protected volatile boolean started;

	public AbstractRemoting(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * 初始化注册中心
	 * 
	 * @return
	 */
	protected Registry initRegistry() {
		RegistryConfig conf = configuration.getRegistryConfig();
		Registry r = null;
		try {
			if (conf != null) {
				r = new ZooKeeperRegistry(conf);
			}
		} catch (Exception e) {
			throw new RuntimeException("Init registry error");
		}
		return r;
	}

	public boolean isStarted() {
		return started;
	}

}
