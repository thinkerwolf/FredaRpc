package com.freda.registry;

import com.freda.common.Net;

public interface RegistryFactory {

	Registry getRegistry(Net net);

}
