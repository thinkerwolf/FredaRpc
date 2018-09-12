package com.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.freda.config.ServerConfig;

public class ServerBean extends ServerConfig implements InitializingBean {
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}

	

}
