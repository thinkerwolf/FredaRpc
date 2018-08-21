package com.freda.remoting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.freda.config.NettyConfig;
import com.freda.registry.Registry;

abstract class AbstractRemoting implements Remoting {

	protected NettyConfig conf;

	protected Set<Registry> registrys = new HashSet<>();

	
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
