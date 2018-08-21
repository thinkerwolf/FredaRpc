package com.freda.config.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.freda.config.spring.ReferenceBean;
import com.freda.config.spring.ServiceBean;

/**
 * 集成到spring中
 * 
 * @author wukai
 *
 */
public class FredaNamespaceHandler extends NamespaceHandlerSupport {
	
	@Override
	public void init() {
		registerBeanDefinitionParser("service", new FredaBeanDefinitionParser(ServiceBean.class));
		registerBeanDefinitionParser("reference", new FredaBeanDefinitionParser(ReferenceBean.class));
		
		
		
		
	}

}
