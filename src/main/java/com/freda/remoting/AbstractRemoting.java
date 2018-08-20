package com.freda.remoting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.freda.common.conf.NettyConfig;
import com.freda.common.conf.ServiceConfig;
import com.freda.registry.Registry;

abstract class AbstractRemoting implements Remoting {

	protected NettyConfig conf;

	protected Set<Registry> registrys = new HashSet<>();

	protected ConcurrentMap<String, ServiceConfig<?>> serviceMap = new ConcurrentHashMap<>();

	protected volatile boolean started;

	public AbstractRemoting(NettyConfig conf) {
		this.conf = conf;
	}

	public void addRegistry(Registry registry) {
		registrys.add(registry);
	}

	public void addRegistrys(Collection<Registry> rs) {
		registrys.addAll(rs);
	}

	@Override
	public void addServiceConfig(ServiceConfig<?> sc) {
		serviceMap.put(sc.getId(), sc);
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

	public Registry getRegistry() {
		Iterator<Registry> iter = registrys.iterator();
		while (iter.hasNext()) {
			Registry registry = iter.next();
			if (registry != null) {
				if (registry.isConnected()) {
					return registry;
				} else {
					iter.remove();
				}
			}
		}
		return null;
	}

	public boolean isStarted() {
		return started;
	}

}
