package com.freda.remoting;

import com.freda.common.Net;
import com.freda.registry.Registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractRemoting implements Remoting {

    protected Net conf;

//    protected Set<Registry> registrys = new HashSet<>();

    protected RemotingHandler handler;

    protected Channel channel;

    public AbstractRemoting(Net conf) {
        this.conf = conf;
    }

    public AbstractRemoting(Net conf, RemotingHandler handler) {
        this.conf = conf;
        this.handler = handler;
    }

//    public void addRegistry(Registry registry) {
//        registrys.add(registry);
//    }
//
//    public void addRegistrys(Collection<Registry> rs) {
//        registrys.addAll(rs);
//    }

//    public Registry getRegistry() {
//        Iterator<Registry> iter = registrys.iterator();
//        while (iter.hasNext()) {
//            Registry registry = iter.next();
//            if (registry != null) {
//                if (registry.isConnected()) {
//                    return registry;
//                } else {
//                    iter.remove();
//
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public Net config() {
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
