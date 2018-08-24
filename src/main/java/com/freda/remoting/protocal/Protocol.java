package com.freda.remoting.protocal;
/**
 * 协议
 * @author wukai
 *
 */
public interface Protocol {
	
	String name();
	
	void send(Object obj);
	
	<T> T refer(Class<T> clazz);
	
}
