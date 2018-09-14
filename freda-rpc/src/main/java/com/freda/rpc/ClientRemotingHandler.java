package com.freda.rpc;

import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRemotingHandler implements RemotingHandler {

	private static final AtomicInteger THREAD_NUM = new AtomicInteger();
	// protected ConcurrentMap<String, Invoker<?>> invokers = new
	// ConcurrentHashMap<>();
	private Map<Integer, RpcFuture> waitResultMap = new ConcurrentHashMap<Integer, RpcFuture>();
	private Executor responseExecutor;

	public ClientRemotingHandler() {
		responseExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("ResponseHandler-Thread-" + THREAD_NUM.incrementAndGet());
				return t;
			}
		});
	}

	@Override
	public RpcFuture send(Remoting remoting, Object msg) {
		RpcFuture rf = new RpcFuture();
		waitResultMap.put(((RequestMessage) msg).getRequestId(), rf);
		remoting.channel().send(msg);
		return rf;
	}

	@Override
	public void received(Remoting remoting, Object msg) {
		// 接收消息
		responseExecutor.execute(new ResponseHandleTask((ResponseMessage) msg));
	}

	// public Invoker<?> getInvoker(String id) {
	// return invokers.get(id);
	// }
	//
	// public void addInvoker(Invoker<?> invoker) {
	// invokers.put(invoker.getType().getName(), invoker);
	// }

	// public void removeInvoker(Invoker<?> invoker) {
	// invokers.remove(invoker.getType().getName());
	// }

	private class ResponseHandleTask implements Runnable {
		private ResponseMessage responseMessage;

		public ResponseHandleTask(ResponseMessage responseMessage) {
			this.responseMessage = responseMessage;
		}

		@Override
		public void run() {
			RpcFuture rf = waitResultMap.remove(responseMessage.getId());
			if (rf != null) {
				rf.setSuccess(responseMessage.isSuccess(), responseMessage.getResult());
			}
		}
	}

}
