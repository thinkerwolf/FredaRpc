package com.freda.config.spring.xml;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.freda.config.spring.ServiceBean;

/**
 * 解析Service
 * 
 * @author wukai
 *
 */
public class FredaServiceBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Class<ServiceBean> beanClass = ServiceBean.class;
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		
		NamedNodeMap attrs = element.getAttributes();
		// set and get is a parameter
		for (Method setter : beanClass.getMethods()) {
			String setName = setter.getName();
			if (setName.length() > 3 && setName.startsWith("set") && Modifier.isPublic(setter.getModifiers())
					&& setter.getParameterTypes().length == 1) {

				String name = setter.getName().substring(0, 2);
				Method getter = null;
				try {
					getter = beanClass.getMethod("get" + name);
				} catch (Exception e) {
					try {
						getter = beanClass.getMethod("is" + name);
					} catch (Exception e1) {
						continue;
					}
				}
				String property = getPropertyName(name);
				Node node = attrs.getNamedItem(property);
				if (node != null) {
					Object reference = null;
					String value = node.getTextContent();
					if ("ref".equals(property)) {
						if (parserContext.getRegistry().containsBeanDefinition(value)) {
							BeanDefinition refBean = parserContext.getRegistry().getBeanDefinition(value);
							if (!refBean.isSingleton()) {
								throw new IllegalStateException(
										"The exported service ref " + value + " must be singleton! Please set the "
												+ value + " bean scope to singleton, eg: <bean id=\"" + value
												+ "\" scope=\"singleton\" ...>");
							}
						}
						reference = new RuntimeBeanReference(value);
						beanDefinition.getPropertyValues().addPropertyValue(property, reference);
					} else if ("interface".equals(property)) {
						
					}
				}
			}
		}

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

	private static String getPropertyName(String name) {
		return new StringBuilder().append(name.substring(0, 1).toLowerCase()).append(name.substring(1, name.length()))
				.toString();
	}

}
