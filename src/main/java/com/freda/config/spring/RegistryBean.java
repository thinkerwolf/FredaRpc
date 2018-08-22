package com.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.freda.common.conf.RegistryConfig;
import com.freda.config.Configuration;

public class RegistryBean extends RegistryConfig implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		Configuration.getInstance().addRegistryConfig(this);
	}
	
}
