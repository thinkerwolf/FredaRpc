package com.thinkerwolf.freda.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thinkerwolf.freda.common.util.ReflectionUtils;

public abstract class InterfaceConfig<T> extends AbstractConfig {

    private static final long serialVersionUID = -8252027163488953256L;
    protected Class<T> interfaceClass;
    protected String interfaceName;
    protected Set<RegistryConfig> registryConfs = new HashSet<RegistryConfig>();
    protected Application conf;

    protected transient T ref;
    protected transient volatile boolean destory;
    protected transient volatile boolean initialized;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceClass.getName();
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

    public void setConf(Application conf) {
        this.conf = conf;
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
