package com.freda.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.freda.common.conf.RegistryConfig;
import com.freda.common.util.ReflectionUtils;

public abstract class InterfaceConfig<T> {

	protected String id;

	protected Class<T> interfaceClass;
	protected String interfaceName;
	protected Set<RegistryConfig> registryConfs = new HashSet<RegistryConfig>();

	protected T ref;

	protected Configuration conf;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}

	@SuppressWarnings("unchecked")
	public void setInterface(String interfaceName) {
		this.interfaceName = interfaceName;
		this.interfaceClass = (Class<T>) ReflectionUtils.getClassByName(interfaceName);
	}

	public String getInterface() {
		return interfaceName;
	}

	public Set<RegistryConfig> getRegistryConfs() {
		return registryConfs;
	}

	public void addRegistryConfs(Collection<RegistryConfig> registrys) {
		this.registryConfs.addAll(registrys);
	}

	public void addRegistryConf(RegistryConfig registry) {
		this.registryConfs.add(registry);
	}

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	/**
	 * export
	 */
	public abstract void export();

	/**
	 * unexport
	 */
	public abstract void unexport();
}
