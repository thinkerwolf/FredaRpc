package com.freda.spring;

import org.springframework.beans.factory.InitializingBean;

import com.freda.common.conf.ServiceConfig;

public class ServiceBean extends ServiceConfig implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
