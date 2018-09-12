package com.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.freda.config.ClientConfig;

public class ClientBean extends ClientConfig implements InitializingBean {
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
}
