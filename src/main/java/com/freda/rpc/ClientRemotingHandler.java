package com.freda.rpc;

import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseFuture;
import com.freda.remoting.ResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRemotingHandler implements RemotingHandler {

	private static final AtomicInteger THREAD_NUM = new AtomicInteger();
	// protected ConcurrentMap<String, Invoker<?>> invokers = new
	// ConcurrentHashMap<>();
	private Map<Integer, ResponseFuture> waitResultMap = new HashMap<Integer, ResponseFuture>();
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
	public ResponseFuture send(Remoting remoting, Object msg) {
		remoting.channel().send(msg);
		ResponseFuture rf = new ResponseFuture();
		waitResultMap.put(((RequestMessage) msg).getId(), rf);
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
			try {
				ResponseFuture rf = waitResultMap.remove(new Integer(responseMessage.getId()));
				if (rf != null) {
					rf.setResult(responseMessage.getResult());
					rf.setSuccess(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
