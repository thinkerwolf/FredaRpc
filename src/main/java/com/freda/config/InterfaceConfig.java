package com.freda.config;

import com.freda.common.util.ReflectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class InterfaceConfig<T> {

	protected String id;
	protected Class<T> interfaceClass;
	protected String interfaceName;
	protected Set<RegistryConfig> registryConfs = new HashSet<RegistryConfig>();
	protected T ref;
	protected Configuration conf;
	protected String registries;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInterfaceClass(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.interfaceName = interfaceClass.getName();
	}

	public Class<T> getInterfaceClass() {
		return interfaceClass;
	}

	public String getInterface() {
		return interfaceName;
	}

	@SuppressWarnings("unchecked")
	public void setInterface(String interfaceName) {
		this.interfaceName = interfaceName;
		this.interfaceClass = (Class<T>) ReflectionUtils.getClassByName(interfaceName);
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

	public String getRegistries() {
		return registries;
	}

	public void setRegistries(String registries) {
		this.registries = registries;
	}

	/**
	 * export
	 */
	public abstract void export() throws Exception;

	/**
	 * unexport
	 */
	public abstract void unexport() throws Exception;
}
