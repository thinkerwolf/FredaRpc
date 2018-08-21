package com.freda.remoting;

import java.util.Collection;

import com.freda.registry.Registry;

public interface Remoting {

	void start();

	void stop();

	void addRegistry(Registry registry);

	void addRegistrys(Collection<Registry> rs);
	
}
