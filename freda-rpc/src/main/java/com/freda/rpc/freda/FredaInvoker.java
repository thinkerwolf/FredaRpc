package com.freda.rpc.freda;

import java.util.concurrent.atomic.AtomicInteger;

import com.freda.remoting.RemotingClient;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.RpcFuture;
import com.freda.rpc.AbstractInvoker;
import com.freda.rpc.Result;
import com.freda.rpc.ResultBuilder;
import com.freda.rpc.RpcException;

public class FredaInvoker<T> extends AbstractInvoker<T> {

	private RemotingClient[] clients;
	
	private AtomicInteger round = new AtomicInteger(0);

	public FredaInvoker(String id, Class<T> type, RemotingClient[] clients) {
		super(id, type);
		this.clients = clients;
	}

	@Override
	public Result invoke(RequestMessage inv) throws RpcException {
		RemotingClient client = null;
		if (clients.length == 1) {
			client = clients[0];
		} else {
			client = clients[Math.abs(round.getAndIncrement() % clients.length)];
		}
		RpcFuture rf = client.handler().send(client, inv);
		try {
			rf.sync();
		} catch (InterruptedException e) {
			throw new RpcException("Rpc future sync exception", e);
		}
		if (rf.isSuccess()) {
			return ResultBuilder.buildSuccessResult(rf.getResult());
		} else {
			return ResultBuilder.buildFailResult();
		}
	}

	@Override
	public synchronized void destory() {
		if (destory) {
			return;
		}
		destory = true;
		if (clients != null) {
			for (int i = 0; i < clients.length; i++) {
				clients[i] = null;
			}
			clients = null;
		}
	}

}
