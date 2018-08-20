package com.freda.remoting;

import com.freda.common.conf.NettyConfig;

public abstract class RemotingClient extends AbstractRemoting {

	public RemotingClient(NettyConfig conf) {
		super(conf);
	}

	/**
	 * 同步调用
	 * 
	 * @return
	 */
	public abstract <T> T invokeSync(Class<T> clazz);

	/**
	 * 异步调用
	 * 
	 * @return
	 */
	public abstract void invokeAsync();

}
