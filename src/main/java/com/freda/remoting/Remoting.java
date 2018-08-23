package com.freda.remoting;

import java.util.Collection;

import com.freda.common.conf.NettyConfig;
import com.freda.registry.Registry;

public interface Remoting {

	void start();

	void stop();

	void addRegistry(Registry registry);

	void addRegistrys(Collection<Registry> rs);
	
	NettyConfig config();
	
	RemotingHandler handler();
	
	Channel channel();
	
	//void sendSync(Object msg);
	
	//void sendAsync(Object msg);
}
