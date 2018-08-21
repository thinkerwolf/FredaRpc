package com.freda.registry;

import com.freda.common.conf.RegistryConfig;

public abstract class AbstractRegistry implements Registry {

	protected RegistryConfig conf;
	
	private boolean connected;
	
	public AbstractRegistry(RegistryConfig conf) {
		this.conf = conf;
	}
	
	@Override
	public synchronized boolean isConnected() {
		return connected;
	}
	
	public synchronized void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public RegistryConfig getConf() {
		return conf;
	}
	
}
