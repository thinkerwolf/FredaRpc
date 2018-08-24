package com.freda.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.freda.common.conf.NetConfig;
import com.freda.remoting.RemotingServer;

/**
 * Service配置
 * 
 * @author wukai
 *
 */
public class ServiceConfig<T> extends InterfaceConfig<T> {

	private Set<NetConfig> nettyConfs = new HashSet<NetConfig>();

	public Set<NetConfig> getNettyConfs() {
		return nettyConfs;
	}

	public void addNettyConfs(Collection<NetConfig> netties) {
		this.nettyConfs.addAll(netties);
	}

	public void addNettyConf(NetConfig netty) {
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
		if (interfaceClass == null) {
			try {
				interfaceClass = (Class<T>) Class.forName(interfaceName == null ? "" : interfaceName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("can't init interfaceClass");
			}
		}
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
		for (NetConfig nc : nettyConfs) {
			RemotingServer rs = conf.getRemotingServer(nc);
			rs.handler().removeServiceConfig(this);
		}
	}
}
