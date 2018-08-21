package com.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.freda.config.ServiceConfig;

/**
 * Spring service bean
 * 
 * @author wukai
 *
 */
public class ServiceBean extends ServiceConfig implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}
