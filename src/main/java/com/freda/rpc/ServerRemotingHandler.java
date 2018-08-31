package com.freda.rpc;

import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingHandler;
import com.freda.remoting.RequestMessage;
import com.freda.remoting.RpcFuture;
import com.freda.remoting.ResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRemotingHandler implements RemotingHandler {

	private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();

	@Override
	public RpcFuture send(Remoting remoting, Object msg) {
		return remoting.channel().send(msg);
	}

	@Override
	public void received(Remoting remoting, Object msg) {
		RequestMessage requestMessage = (RequestMessage) msg;
		ResponseMessage responseMessage = new ResponseMessage();
		try {
			Exporter<?> exporter = exporters.get(requestMessage.getClazzName());
			responseMessage.setId(requestMessage.getId());
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

		send(remoting, responseMessage);
	}

	public void addExeporter(Exporter<?> e) {
		exporters.put(e.id(), e);
	}

}
