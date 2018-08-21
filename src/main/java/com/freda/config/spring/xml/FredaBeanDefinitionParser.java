package com.freda.config.spring.xml;

import java.lang.reflect.Method;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.freda.config.ServiceConfig;
import com.freda.config.spring.ServiceBean;

/**
 * 解析Freda标签
 * 
 * @author wukai
 *
 */
public class FredaBeanDefinitionParser implements BeanDefinitionParser {

	public static final String ID = "id";

	private Class<?> beanClass;

	public FredaBeanDefinitionParser(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		
		// set get
		for (Method setMet : beanClass.getMethods()) {
			
			
			
		}

		NamedNodeMap attrs = element.getAttributes();
		ManagedMap<String, TypedStringValue> parameters = new ManagedMap<String, TypedStringValue>();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node node = attrs.item(i);
			String name = node.getLocalName();
			String value = node.getTextContent();
			parameters.put(name, new TypedStringValue(value, String.class));
		}
		beanDefinition.getPropertyValues().addPropertyValue("parameters", parameters);
		return beanDefinition;
	}

}
