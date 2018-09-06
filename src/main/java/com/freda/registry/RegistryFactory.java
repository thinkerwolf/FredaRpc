package com.freda.registry;

import com.freda.common.conf.RegistryConfig;

public interface RegistryFactory {

	Registry getRegistry(RegistryConfig registryConf);

}
