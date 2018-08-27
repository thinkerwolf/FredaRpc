package com.freda.rpc;

import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingServer;
import com.freda.remoting.netty.NettyClient;
import com.freda.remoting.netty.NettyServer;

import java.util.List;

public class RemotingFactory {

    private static final RemotingFactory INSTANCE = new RemotingFactory();

    public static RemotingFactory getInstance() {
        return INSTANCE;
    }

    public RemotingClient createRemotingClient(NetConfig nc, List<Registry> registries) {
        ClientRemotingHandler handler = new ClientRemotingHandler();
        RemotingClient remoting = new NettyClient(nc, handler);
        if (registries != null) {
            remoting.addRegistrys(registries);
        }
        return remoting;
    }

    public RemotingServer createRemotingServer(NetConfig nc, List<Registry> registries) {
        ServerRemotingHandler handler = new ServerRemotingHandler();
        RemotingServer remoting = new NettyServer(nc, handler);
        if (registries != null) {
            remoting.addRegistrys(registries);
        }
        return remoting;
    }

}
