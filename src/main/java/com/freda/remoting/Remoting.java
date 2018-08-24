package com.freda.remoting;

import java.util.Collection;

import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;
import com.freda.remoting.protocal.Protocol;

public interface Remoting {

	void start();

	void stop();

	void addRegistry(Registry registry);

	void addRegistrys(Collection<Registry> rs);
	
	NetConfig config();
	
	RemotingHandler handler();
	
	Channel channel();
	
	Protocol protocol();
}
