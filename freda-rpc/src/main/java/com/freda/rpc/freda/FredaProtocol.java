package com.freda.rpc.freda;

import com.freda.common.Net;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingServer;
import com.freda.rpc.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FredaProtocol extends AbstractProtocol {

    public static final String NAME = "freda";
    public static FredaProtocol INSTANCE = new FredaProtocol();
    protected Map<String, RemotingClient> remotingClientMap = new ConcurrentHashMap<>();
    protected Map<String, RemotingServer> remotingServerMap = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public <T> Invoker<T> refer(String id, Class<T> type, List<Net> ncs) {
        RemotingClient[] clients = getClients(ncs);
        FredaInvoker<T> invoker = new FredaInvoker<>(id == null ? type.getName() : id, type, clients);
        invokers.put(id == null ? type.getName() : id, invoker);
        return invoker;
    }

    private <T> RemotingClient[] getClients(List<Net> ncs) {
        RemotingClient[] clients = new RemotingClient[ncs.size()];
        for (Net nc : ncs) {
            RemotingClient remoting = remotingClientMap.get(nc.key());
            if (remoting == null) {
                remoting = RemotingFactory.getInstance().createRemotingClient(nc, null);
                remoting.start();
                remotingClientMap.put(nc.key(), remoting);
            }
            clients[0] = remoting;
        }
        return clients;
    }

    private RemotingServer getServer(Net nc) {
        RemotingServer server = remotingServerMap.get(nc.key());
        if (server == null) {
            server = RemotingFactory.getInstance().createRemotingServer(nc, null);
            server.start();
            remotingServerMap.put(nc.key(), server);
        }
        return server;
    }

    @Override
    public <T> Exporter<T> export(String id, Class<T> type, T ref, Net nc) {
        RemotingServer server = getServer(nc);
        ServerRemotingHandler handler = (ServerRemotingHandler) server.handler();
        FredaExporter<T> e = new FredaExporter<T>(id, type, ref, handler);
        handler.addExporter(e);
        return e;
    }

}
