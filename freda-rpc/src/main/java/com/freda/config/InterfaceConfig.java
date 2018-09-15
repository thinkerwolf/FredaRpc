package com.freda.config;

import com.freda.common.util.ReflectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class InterfaceConfig<T> extends AbstractConfig {

    private static final long serialVersionUID = -8252027163488953256L;
    protected Class<T> interfaceClass;
    protected String interfaceName;
    protected Set<RegistryConfig> registryConfs = new HashSet<RegistryConfig>();
    protected Configuration conf;

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

    public void setConf(Configuration conf) {
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
