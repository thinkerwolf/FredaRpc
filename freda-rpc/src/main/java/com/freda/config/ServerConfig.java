package com.freda.config;

import java.util.List;

public class ServerConfig extends AbstractNetworkConfig {

	private static final long serialVersionUID = 5587883607556110077L;

	private List<RegistryConfig> registryConfigs;

	public List<RegistryConfig> getRegistryConfigs() {
		return registryConfigs;
	}

	public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
		this.registryConfigs = registryConfigs;
	}

}
