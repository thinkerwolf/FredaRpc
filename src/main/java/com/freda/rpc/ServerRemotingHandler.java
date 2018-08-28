package com.freda.rpc;

import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.ResponseFuture;
import com.freda.remoting.ResponseMessage;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRemotingHandler implements RemotingHandler {

	private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();

	@Override
	public ResponseFuture send(Remoting remoting, Object msg) {
		remoting.channel().send(msg);
		return null;
	}

	@Override
	public void received(Remoting remoting, Object msg) {
		RequestMessage requestMessage = (RequestMessage) msg;
		ResponseMessage responseMessage = new ResponseMessage();
		try {
			Object obj = null;
			Exporter<?> exporter = exporters.get(requestMessage.getClazzName());
			responseMessage.setId(requestMessage.getId());
			if (exporter != null) {
				obj = exporter.ref();
				Method method = obj.getClass().getMethod(requestMessage.getMethodName(),
						requestMessage.getParameterTypes());
				method.setAccessible(true);
				Object result = method.invoke(obj, requestMessage.getArgs());
				responseMessage.setSuccess(true);
				responseMessage.setResult(result);
			} else {
				responseMessage.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage.setSuccess(false);
		}
		
		send(remoting, responseMessage);
	}

	public void addExeporter(Exporter<?> e) {
		exporters.put(e.id(), e);
	}

}