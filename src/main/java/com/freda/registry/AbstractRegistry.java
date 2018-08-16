package com.freda.registry;

import com.freda.common.conf.RegistryConfig;

public abstract class AbstractRegistry implements Registry {

	protected RegistryConfig conf;

	public AbstractRegistry(RegistryConfig conf) {
		this.conf = conf;
	}
}
