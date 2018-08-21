package com.freda.config.spring;

import com.freda.config.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.freda.config.ServiceConfig;

/**
 * Spring service bean
 * 
 * @author wukai
 *
 */
public class ServiceBean extends ServiceConfig implements InitializingBean, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		Configuration.getInstance().getNettyServerConfigs();



	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
