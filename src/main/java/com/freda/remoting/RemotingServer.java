package com.freda.remoting;


import com.freda.common.conf.NetConfig;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.rpc.ServerRemotingHandler;

public abstract class RemotingServer extends AbstractRemoting {

    public RemotingServer(NetConfig conf) {
        super(conf);
    }

    public RemotingServer(NetConfig conf, RemotingHandler handler) {
        super(conf, handler);
    }

    protected void registerSelf(Server server) {
        if (registrys != null) {
            for (Registry registry : registrys) {
                try {
                    registry.register(server);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ServerRemotingHandler handler() {
        return (ServerRemotingHandler) super.handler();
    }

}
