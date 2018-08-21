package com.freda.config;

import com.freda.common.conf.NettyConfig;

/**
 * 调用者配置
 * 
 * @author wukai
 *
 * @param <T>
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {

	private NettyConfig nettyConf;

	public NettyConfig getNettyConf() {
		return nettyConf;
	}

	public void setNettyConf(NettyConfig nettyConf) {
		this.nettyConf = nettyConf;
	}

	@Override
	public void export() {
		conf.addReferenceConfig(this);
	}

	@Override
	public void unexport() {

	}

}
