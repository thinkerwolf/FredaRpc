package com.freda.remoting;

import com.freda.common.conf.NetConfig;

public abstract class RemotingClient extends AbstractRemoting {

	public RemotingClient(NetConfig conf, RemotingHandler handler) {
		super(conf, handler);
		if (handler != null) {
			doInit();
		}
	}

//	@Override
//	public ClientRemotingHandler handler() {
//		return (ClientRemotingHandler) super.handler();
//	}
	
	protected abstract void doInit();
	
	protected abstract Channel doConnect();
	
	@Override
	public Channel start() {
		return doConnect();
	}
	
}
