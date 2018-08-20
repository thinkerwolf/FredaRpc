package com.freda.common.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * Service配置
 * 
 * @author wukai
 *
 */
public class ServiceConfig<T> {
	/**
	 * Service对应的实体
	 */
	private T ref;
	/**
	 * Service id
	 */
	private String id;
	/**
	 * class
	 */
	private Class<T> interfaceClass;
	/**
	 * 是否是服务端
	 */
	private boolean server;
	/**
	 * 每个接口的注册者
	 */
	private List<RegistryConfig> registrys = new ArrayList<RegistryConfig>();
	/**
	 * Netty配置
	 */
	private List<NettyConfig> netties = new ArrayList<NettyConfig>();
	
	private Configuration conf;
	
	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	public List<RegistryConfig> getRegistrys() {
		return registrys;
	}

	public List<NettyConfig> getNetties() {
		return netties;
	}
	
	public void addRegistrys(List<RegistryConfig> registrys) {
		this.registrys.addAll(registrys);
	}

	public void addsetNetties(List<NettyConfig> netties) {
		this.netties.addAll(netties);
	}
	
	public void addRegistry(RegistryConfig registry) {
		this.registrys.add(registry);
	}

	public void addNetty(NettyConfig netty) {
		this.netties.add(netty);
	}
	
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public String toString() {
		return "id#" + id + ", clazz#" + interfaceClass;
	}

	/**
	 * 暴漏接口
	 */
	public void export() {
		if (isServer()) {
			checkRef();
		}
		this.conf.addServiceConfig(this);
	}

	private void checkRef() {
		if (!interfaceClass.isInstance(ref)) {
			throw new RuntimeException("ref is not the instance of " + interfaceClass.getName());
		}

	}

}
