package com.thinkerwolf.freda.registry;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.common.SLI;

@SLI("zookeeper")
public interface RegistryFactory {

	Registry getRegistry(Net net);

}
