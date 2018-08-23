package com.freda.remoting;

import com.freda.common.conf.NettyConfig;

public abstract class RemotingClient extends AbstractRemoting {

	public RemotingClient(NettyConfig conf) {
		super(conf);
	}

	public RemotingClient(NettyConfig conf, RemotingHandler handler) {
		super(conf, handler);
	}

	/**
	 * 同步调用
	 * 
	 * @return
	 */
	public abstract <T> T sendSync(Class<T> clazz);

	/**
	 * 异步调用
	 * 
	 * @return
	 */
	public abstract void invokeAsync();
	
	@Override
	public ClientRemotingHandler handler() {
		return (ClientRemotingHandler) super.handler();
	}
	
}
