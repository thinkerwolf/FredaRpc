package com.freda.config.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.freda.config.ReferenceConfig;

/**
 * spring reference bean
 * 
 * @author wukai
 *
 */
public class ReferenceBean extends ReferenceConfig implements InitializingBean, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {



	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
