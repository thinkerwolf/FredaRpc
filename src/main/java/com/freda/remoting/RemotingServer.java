package com.freda.remoting;

import com.freda.common.conf.Configuration;
import com.freda.registry.Server;

public abstract class RemotingServer extends AbstractRemoting {

	public RemotingServer(Configuration configuration) {
		super(configuration);
	}

	protected void registerSelf(Server server) {
		if (registry != null) {
			try {
				registry.register(server);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
