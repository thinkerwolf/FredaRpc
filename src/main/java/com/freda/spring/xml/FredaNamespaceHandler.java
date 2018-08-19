package com.freda.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 集成到spring中
 * 
 * @author wukai
 *
 */
public class FredaNamespaceHandler extends NamespaceHandlerSupport {
	
	@Override
	public void init() {
		registerBeanDefinitionParser("service", new FredaServiceBeanDefinitionParser());
		// registerBeanDefinitionParser("registry", new
		// FredaServiceBeanDefinitionParser());
	}

}
