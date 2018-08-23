package com.freda.remoting;

import java.util.List;

import com.freda.common.conf.NettyConfig;
import com.freda.registry.Registry;
import com.freda.remoting.netty.NettyClient;
import com.freda.remoting.netty.NettyServer;

public class RemotingFactory {

	private static final RemotingFactory INSTANCE = new RemotingFactory();

	public static RemotingFactory getInstance() {
		return INSTANCE;
	}

	public RemotingClient createRemotingClient(NettyConfig nc, List<Registry> registries) {
		ClientRemotingHandler handler = new ClientRemotingHandler();
		RemotingClient remoting = new NettyClient(nc, handler);
		if (registries != null) {
			remoting.addRegistrys(registries);
		}
		return remoting;
	}
	
	public RemotingServer createRemotingServer(NettyConfig nc, List<Registry> registries) {
		ServerRemotingHandler handler = new ServerRemotingHandler();
		RemotingServer remoting = new NettyServer(nc, handler);
		if (registries != null) {
			remoting.addRegistrys(registries);
		}
		return remoting;
	}

}
