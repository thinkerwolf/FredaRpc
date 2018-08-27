package com.freda.remoting;

import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;

import java.util.Collection;

public interface Remoting {

    void start();

    void stop();

    void addRegistry(Registry registry);

    void addRegistrys(Collection<Registry> rs);

    NetConfig config();

    RemotingHandler handler();

    Channel channel();

}
