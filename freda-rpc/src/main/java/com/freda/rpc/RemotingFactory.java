package com.freda.rpc;

import com.freda.common.Net;
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

    public RemotingClient createRemotingClient(Net nc, List<Registry> registries) {
        ClientRemotingHandler handler = new ClientRemotingHandler(ResponseMessage.class);
        RemotingClient remoting = new NettyClient(nc, handler);
        return remoting;
    }

    public RemotingServer createRemotingServer(Net nc, List<Registry> registries) {
        ServerRemotingHandler handler = new ServerRemotingHandler(RequestMessage.class);
        RemotingServer remoting = new NettyServer(nc, handler);
        return remoting;
    }

}
