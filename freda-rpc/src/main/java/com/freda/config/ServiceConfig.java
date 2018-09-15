package com.freda.config;

import com.freda.common.Net;
import com.freda.common.ServiceLoader;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.registry.ServerNameBuilder;
import com.freda.rpc.Exporter;
import com.freda.rpc.Protocol;

import java.util.LinkedList;
import java.util.List;

/**
 * Service配置
 *
 * @author wukai
 */
public class ServiceConfig<T> extends InterfaceConfig<T> {

    private static final long serialVersionUID = 7810449405775349100L;
    protected String servers;
    private List<Exporter<T>> exporters = new LinkedList<>();
    private List<ServerConfig> serverConfigs;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public void setServerConfigs(List<ServerConfig> serverConfigs) {
        this.serverConfigs = serverConfigs;
    }

    @Override
    public String toString() {
        return "id#" + id + ", clazz#" + interfaceClass;
    }

    /**
     * 暴漏接口
     */
    @SuppressWarnings("unchecked")
    public synchronized void export() throws Exception {
        if (initialized) {
            return;
        }
        initialized = true;
        if (interfaceClass == null) {
            try {
                interfaceClass = (Class<T>) Class.forName(interfaceName == null ? "" : interfaceName);
            } catch (ClassNotFoundException e) {
                throw new ServiceConfigException("can't init interfaceClass [" + interfaceName + "]");
            }
        }
        checkRef();
        for (ServerConfig sc : serverConfigs) {
            String serverName = ServerNameBuilder.getInstance().generateServerName(null, sc.getHost(), sc.getPort());
            List<Registry> registries = conf.handleRegistries(sc.getRegistryConfigs());
            for (Registry r : registries) {
                r.register(new Server(serverName, sc.getHost(), sc.getPort(), sc.getProtocol()));
            }
            Protocol protocol = ServiceLoader.getService(sc.getProtocol(), Protocol.class);
            Exporter<T> e = protocol.export(getId(), interfaceClass, ref, new Net(sc.getHost(), sc.getPort(), sc.getProtocol()));
            exporters.add(e);
        }
    }

    private void checkRef() {
        if (!interfaceClass.isInstance(ref)) {
            throw new ServiceConfigException("ref is not the instance of " + interfaceClass.getName());
        }
    }

    @Override
    public synchronized void unexport() {
        if (destory) {
            return;
        }
        destory = true;
        if (this.exporters != null) {
            for (Exporter<T> exporter : exporters) {
                exporter.destory();
            }
        }
    }
}
