package com.freda.remoting.protocal;

/**
 * Http协议
 * 
 * @author wukai
 *
 */
public class HttpProtocol implements Protocol {
	public static final String NAME = "http";
	
	@Override
	public String name() {
		return NAME;
	}

	@Override
	public void send(Object obj) {

	}

	@Override
	public <T> T refer(Class<T> clazz) {
		return null;
	}

}
