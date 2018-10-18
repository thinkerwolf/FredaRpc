package com.thinkerwolf.freda.rpc;

import java.util.List;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.registry.Registry;
import com.thinkerwolf.freda.remoting.RemotingClient;
import com.thinkerwolf.freda.remoting.RemotingServer;
import com.thinkerwolf.freda.remoting.netty.NettyClient;
import com.thinkerwolf.freda.remoting.netty.NettyServer;

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
