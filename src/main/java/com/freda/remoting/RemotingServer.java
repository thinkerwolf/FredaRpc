package com.freda.remoting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.freda.common.conf.NettyConfig;
import com.freda.config.ServiceConfig;
import com.freda.registry.Registry;
import com.freda.registry.Server;

public abstract class RemotingServer extends AbstractRemoting {
	
	protected ConcurrentMap<String, ServiceConfig<?>> serviceMap = new ConcurrentHashMap<>();
	
	public RemotingServer(NettyConfig conf) {
		super(conf);
	}

	protected void registerSelf(Server server) {
		if (registrys != null) {
			for (Registry registry : registrys) {
				try {
					registry.register(server);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void addServiceConfig(ServiceConfig<?> sc) {
		serviceMap.put(sc.getId(), sc);
	}
	
	public void removeServiceConfig(ServiceConfig<?> sc) {
		serviceMap.remove(sc.getId());
	}
	
	public ServiceConfig<?> getServiceConfig(Class<?> clazz) {
		for (ServiceConfig<?> sc : serviceMap.values()) {
			if (sc.getInterfaceClass().equals(clazz)) {
				return sc;
			}
		}
		return null;
	}

	public ServiceConfig<?> getServiceConfig(String id) {
		return serviceMap.get(id);
	}
}
