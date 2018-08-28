package com.freda.remoting;

import com.freda.common.conf.NetConfig;

public abstract class RemotingClient extends AbstractRemoting {

	public RemotingClient(NetConfig conf) {
		super(conf);
	}

	public RemotingClient(NetConfig conf, RemotingHandler handler) {
		super(conf, handler);
	}

//	@Override
//	public ClientRemotingHandler handler() {
//		return (ClientRemotingHandler) super.handler();
//	}

}
