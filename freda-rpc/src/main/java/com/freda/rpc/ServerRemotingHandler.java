package com.freda.rpc;

import com.freda.common.concurrent.DefaultPromise;
import com.freda.common.concurrent.Future;
import com.freda.remoting.Channel;
import com.freda.remoting.RemotingHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRemotingHandler implements RemotingHandler {

    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();
    
    private Class<?> decodeClass;
    
    public ServerRemotingHandler(Class<?> decodeClass) {
		this.decodeClass = decodeClass;
	}

	@Override
    public Future<?> send(Channel channel, Object msg) {
        DefaultPromise<Object> rf = new DefaultPromise<Object>();
        channel.send(msg);
        return rf;
    }

    @Override
    public void received(Channel channel, Object msg) {
        RequestMessage requestMessage = (RequestMessage) msg;
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            Exporter<?> exporter = exporters.get(requestMessage.getClazzName());
            responseMessage.setId(requestMessage.getRequestId());
            if (exporter != null) {
                Object result = exporter.invoke(requestMessage.getMethodName(), requestMessage.getParameterTypes(),
                        requestMessage.getArgs());
                responseMessage.setSuccess(true);
                responseMessage.setResult(result);
            } else {
                responseMessage.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.setSuccess(false);
        }
        send(channel, responseMessage);
    }

    public void addExporter(Exporter<?> e) {
        exporters.put(e.id(), e);
    }

    public void removeExporter(Exporter<?> e) {
        exporters.remove(e.id());
    }

	@Override
	public Class<?> decodeClass() {
		return decodeClass;
	}

}
