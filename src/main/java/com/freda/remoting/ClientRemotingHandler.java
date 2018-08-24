package com.freda.remoting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.freda.common.proxy.ProxyHandler;
import com.freda.common.util.ProxyUtils;
import com.freda.config.ReferenceConfig;

public class ClientRemotingHandler implements RemotingHandler {

	protected ConcurrentMap<String, ReferenceConfig<?>> referenceMap = new ConcurrentHashMap<>();
	private Map<Integer, ResponseFuture> waitResultMap = new HashMap<Integer, ResponseFuture>();
	private static final AtomicInteger ID_GENARETOR = new AtomicInteger();
	private static final AtomicInteger THREAD_NUM = new AtomicInteger();
	private Thread _responseHandleThread;
	private BlockingQueue<ResponseMessage> responseMessages = new LinkedBlockingQueue<ResponseMessage>();
	private boolean responseStart;

	public ClientRemotingHandler() {
		_responseHandleThread = new Thread(new ResponseHandleTask(),
				"ResponseHandler-Thread-" + THREAD_NUM.incrementAndGet());
		_responseHandleThread.setDaemon(false);
		_responseHandleThread.setPriority(Thread.NORM_PRIORITY);
		responseStart = false;
	}

	@SuppressWarnings("unchecked")
	public <T> ReferenceConfig<T> getReferenceConfig(Class<T> clazz) {
		for (ReferenceConfig<?> rc : referenceMap.values()) {
			if (clazz.equals(rc.getInterfaceClass())) {
				return (ReferenceConfig<T>) rc;
			}
		}
		return null;
	}

	@Override
	public Object send(Remoting remoting, Object msg) {
		if (!responseStart) {
			synchronized (_responseHandleThread) {
				if (!responseStart) {
					_responseHandleThread.start();
					responseStart = true;
				}
			}
		}
		Class<?> clazz = (Class<?>) msg;
		final ReferenceConfig refConfig = getReferenceConfig(clazz);
		if (refConfig == null) {
			return null;
		}
		if (refConfig.getRef() == null) {
			Object ref = ProxyUtils.newProxy(clazz, new ProxyHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					final int id = ID_GENARETOR.incrementAndGet();
					RequestMessage rm = new RequestMessage();
					rm.setId(id);
					rm.setClazzName(refConfig.getId());
					rm.setMethodName(method.getName());
					rm.setParameterTypes(method.getParameterTypes());
					rm.setArgs(args);
					ResponseFuture rf = new ResponseFuture();
					waitResultMap.put(id, rf);
					remoting.channel().send(rm);
					rf.sync();
					return rf.getResult();
				}
			});
			refConfig.setRef(ref);
		}
		return refConfig.getRef();
	}

	@Override
	public void received(Remoting remoting, Object msg) {
		// 接收消息
		responseMessages.offer((ResponseMessage) msg);
	}

	public ReferenceConfig<?> getReferenceConfig(String id) {
		return referenceMap.get(id);
	}

	public void addReferenceConfig(ReferenceConfig<?> reConfig) {
		referenceMap.put(reConfig.getId(), reConfig);
	}

	public void removeReferenceConfig(ReferenceConfig<?> reConfig) {
		referenceMap.remove(reConfig.getId());
	}

	// 轮询处理
	private class ResponseHandleTask implements Runnable {
		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					ResponseMessage rm = responseMessages.take();
					ResponseFuture rf = waitResultMap.remove(new Integer(rm.getId()));
					if (rf != null) {
						rf.setResult(rm.getResult());
						rf.setSuccess(null);
					}
				}

			} catch (Exception e) {

			}
		}
	}

}
