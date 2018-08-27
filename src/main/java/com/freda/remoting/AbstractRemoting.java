package com.freda.remoting;

import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractRemoting implements Remoting {

    protected NetConfig conf;

    protected Set<Registry> registrys = new HashSet<>();

    protected volatile boolean started;

    protected RemotingHandler handler;

    protected Channel channel;

    public AbstractRemoting(NetConfig conf) {
        this.conf = conf;
    }

    public AbstractRemoting(NetConfig conf, RemotingHandler handler) {
        this.conf = conf;
        this.handler = handler;
    }

    public void addRegistry(Registry registry) {
        registrys.add(registry);
    }

    public void addRegistrys(Collection<Registry> rs) {
        registrys.addAll(rs);
    }

    public Registry getRegistry() {
        Iterator<Registry> iter = registrys.iterator();
        while (iter.hasNext()) {
            Registry registry = iter.next();
            if (registry != null) {
                if (registry.isConnected()) {
                    return registry;
                } else {
                    iter.remove();

                }
            }
        }
        return null;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public NetConfig config() {
        return conf;
    }

    @Override
    public RemotingHandler handler() {
        return handler;
    }

    @Override
    public Channel channel() {
        return channel;
    }

}
