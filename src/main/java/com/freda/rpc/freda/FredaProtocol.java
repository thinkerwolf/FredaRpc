package com.freda.rpc.freda;

import java.util.List;

import com.freda.common.conf.NetConfig;
import com.freda.remoting.RemotingClient;
import com.freda.rpc.AbstractProtocol;
import com.freda.rpc.Exporter;
import com.freda.rpc.Invoker;
import com.freda.rpc.RemotingFactory;

public class FredaProtocol extends AbstractProtocol {

	public static final String NAME = "freda";
	public static FredaProtocol INSTANCE = new FredaProtocol();

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public void send(Object obj) {

	}

	@Override
	public <T> Invoker<T> refer(Class<T> type, List<NetConfig> ncs) {
		RemotingClient[] clients = getClients(ncs);
		FredaInvoker<T> invoker = new FredaInvoker<>(type, clients);
		invokers.put(type.getName(), invoker);
		return invoker;
	}

	private <T> RemotingClient[] getClients(List<NetConfig> ncs) {
		RemotingClient[] clients = new RemotingClient[ncs.size()];
		for (NetConfig nc : ncs) {
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

	@Override
	public <T> Exporter<T> export(Class<T> type, T ref, List<NetConfig> ncs) {

		return null;
	}

}
