package com.thinkerwolf.freda.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkerwolf.freda.remoting.Channel;

public class ServerChannelRunnable extends ChannelRunnable {
	private static final Logger logger = LoggerFactory.getLogger(ServerChannelRunnable.class);

	private ServerRemotingHandler handler;

	public ServerChannelRunnable(Channel channel, Object msg, ServerRemotingHandler handler) {
		super(channel, msg);
		this.handler = handler;
	}

	@Override
	public void run() {
		RequestMessage requestMessage = (RequestMessage) msg;
		ResponseMessage responseMessage = new ResponseMessage();
		try {
			Exporter<?> exporter = handler.exporters.get(requestMessage.getId());
			responseMessage.setId(requestMessage.getRequestId());
			if (exporter != null) {
				Object result = exporter.invoke(channel.net(), requestMessage.getMethodName(),
						requestMessage.getParameterTypes(), requestMessage.getArgs());
				responseMessage.setSuccess(true);
				responseMessage.setResult(result);
			} else {
				responseMessage.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("server export", e);
			responseMessage.setSuccess(false);
		}
		handler.send(channel, responseMessage);
	}

}
