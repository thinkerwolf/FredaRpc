package com.freda.remoting;

import com.freda.common.Net;
import com.freda.registry.Registry;

import java.util.Collection;

public interface Remoting {

    void stop();

    void addRegistry(Registry registry);

    void addRegistrys(Collection<Registry> rs);

    Net config();

    RemotingHandler handler();

    Channel channel();
    
    Channel start();
    
}
