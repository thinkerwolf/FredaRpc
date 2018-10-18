package com.thinkerwolf.freda.remoting;

import com.thinkerwolf.freda.common.Net;

abstract class AbstractRemoting implements Remoting {

    protected Net net;

//    protected Set<Registry> registrys = new HashSet<>();

    protected RemotingHandler handler;

    protected Channel channel;

    public AbstractRemoting(Net net) {
        this.net = net;
    }

    public AbstractRemoting(Net net, RemotingHandler handler) {
        this.net = net;
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
        return net;
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
