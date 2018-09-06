package com.freda.config;

import com.freda.common.ServiceLoader;
import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.registry.ServerNameBuilder;
import com.freda.rpc.Exporter;
import com.freda.rpc.Protocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Service配置
 *
 * @author wukai
 */
public class ServiceConfig<T> extends InterfaceConfig<T> {

	private Set<NetConfig> nettyConfs = new HashSet<NetConfig>();

	private List<Exporter<T>> exporters = new LinkedList<>();

	private List<Registry> registries;

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
	@SuppressWarnings("unchecked")
	public void export() throws Exception {
		if (interfaceClass == null) {
			try {
				interfaceClass = (Class<T>) Class.forName(interfaceName == null ? "" : interfaceName);
			} catch (ClassNotFoundException e) {
				throw new ServiceConfigException("can't init interfaceClass [" + interfaceName + "]");
			}
		}
		checkRef();

		this.registries = conf.handleRegistries(this.registryConfs);
		for (NetConfig nc : nettyConfs) {
			if (nc.isUseable()) {
				String serverName = ServerNameBuilder.getInstance().generateServerName(null, nc.getIp(), nc.getPort());
				for (Registry r : registries) {
					r.register(new Server(serverName, nc.getIp(), nc.getPort(), nc.getProtocol()));
				}
				Protocol protocol = ServiceLoader.getService(nc.getProtocol(), Protocol.class);
				Exporter<T> e = protocol.export(getId(), interfaceClass, ref, nc);
				exporters.add(e);
			}
		}
	}

	private void checkRef() {
		if (!interfaceClass.isInstance(ref)) {
			throw new ServiceConfigException("ref is not the instance of " + interfaceClass.getName());
		}
	}

	@Override
	public void unexport() {

	}
}
