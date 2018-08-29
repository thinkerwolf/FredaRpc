package com.freda.rpc.freda;

import java.util.concurrent.atomic.AtomicInteger;

import com.freda.remoting.RemotingClient;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseFuture;
import com.freda.rpc.AbstractInvoker;
import com.freda.rpc.RpcException;

public class FredaInvoker<T> extends AbstractInvoker<T> {

	private RemotingClient[] clients;

	private AtomicInteger round = new AtomicInteger(0);

	public FredaInvoker(String id, Class<T> type, RemotingClient[] clients) {
		super(id, type);
		this.clients = clients;
	}

	@Override
	public Object invoke(RequestMessage inv) throws RpcException {
		RemotingClient client = null;
		if (clients.length == 1) {
			client = clients[0];
		} else {
			client = clients[Math.abs(round.getAndIncrement() % clients.length)];
		}
		ResponseFuture rf = client.handler().send(client, inv);
		try {
			rf.sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return rf.getResult();
	}

}
