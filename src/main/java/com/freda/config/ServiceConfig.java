package com.freda.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.freda.common.conf.NettyConfig;

/**
 * Service配置
 * 
 * @author wukai
 *
 */
public class ServiceConfig<T> extends InterfaceConfig<T>{
	
	private Set<NettyConfig> nettyConfs = new HashSet<NettyConfig>();

	public Set<NettyConfig> getNettyConfs() {
		return nettyConfs;
	}

	public void addNettyConfs(Collection<NettyConfig> netties) {
		this.nettyConfs.addAll(netties);
	}

	public void addNettyConf(NettyConfig netty) {
		this.nettyConfs.add(netty);
	}

	@Override
	public String toString() {
		return "id#" + id + ", clazz#" + interfaceClass;
	}

	/**
	 * 暴漏接口
	 */
	public void export() {
		checkRef();
		this.conf.addServiceConfig(this);
	}

	private void checkRef() {
		if (!interfaceClass.isInstance(ref)) {
			throw new RuntimeException("ref is not the instance of " + interfaceClass.getName());
		}

	}

	@Override
	public void unexport() {
		
	}

}
