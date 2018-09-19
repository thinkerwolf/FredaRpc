package com.freda.registry;

import com.freda.common.Net;
import com.freda.common.SLI;

@SLI("zookeeper")
public interface RegistryFactory {

	Registry getRegistry(Net net);

}
