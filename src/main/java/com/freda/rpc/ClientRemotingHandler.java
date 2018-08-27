package com.freda.rpc;

import com.freda.config.ReferenceConfig;
import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.ResponseFuture;
import com.freda.remoting.ResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRemotingHandler implements RemotingHandler {

	private static final AtomicInteger THREAD_NUM = new AtomicInteger();
	protected ConcurrentMap<String, Invoker<?>> invokers = new ConcurrentHashMap<>();
	private Map<Integer, ResponseFuture> waitResultMap = new HashMap<Integer, ResponseFuture>();
	private Executor responseExecutor;
	
	public ClientRemotingHandler() {
		responseExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("ResponseHandler-Thread-" + THREAD_NUM.incrementAndGet());
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> ReferenceConfig<T> getInvoker(Class<T> clazz) {
		return (ReferenceConfig<T>) invokers.get(clazz.getName());
	}

	@Override
	public Object send(Remoting remoting, Object msg) {
		remoting.channel().send(msg);
		return null;
	}

	@Override
	public void received(Remoting remoting, Object msg) {
		// 接收消息
		responseExecutor.execute(new ResponseHandleTask((ResponseMessage) msg));
	}

	public Invoker<?> getInvoker(String id) {
		return invokers.get(id);
	}

	public void addReferenceConfig(Invoker<?> invoker) {
		invokers.put(invoker.getType().getName(), invoker);
	}

	public void removeInvoker(Invoker<?> invoker) {
		invokers.remove(invoker.getType().getName());
	}

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

			}
		}
	}

}
