package com.freda.rpc;

import com.freda.common.concurrent.DefaultPromise;
import com.freda.common.concurrent.Future;
import com.freda.remoting.Channel;
import com.freda.remoting.RemotingHandler;

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
    private Map<Integer, DefaultPromise<?>> waitResultMap = new ConcurrentHashMap<Integer, DefaultPromise<?>>();
    private Executor responseExecutor;
    
    private Class<?> decodeClass;
    
    public ClientRemotingHandler(Class<?> decodeClass) {
    	this.decodeClass = decodeClass;
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
    public Future<?> send(Channel channel, Object msg) {
    	RequestMessage rm = (RequestMessage) msg;
        DefaultPromise<Object> rf = new DefaultPromise<Object>();
        waitResultMap.put(rm.getRequestId(), rf);
        channel.send(msg);
        return rf;
    }

    @Override
    public void received(Channel channel, Object msg) {
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

        @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
        public void run() {
            DefaultPromise rf = waitResultMap.remove(responseMessage.getRequestId());
            if (rf != null) {
                if (responseMessage.isSuccess()) {
                    rf.setSuccess(responseMessage.getResult());
                } else {
                    rf.setFailure(null);
                }
            }
        }
    }

	@Override
	public Class<?> decodeClass() {
		return decodeClass;
	}

}
