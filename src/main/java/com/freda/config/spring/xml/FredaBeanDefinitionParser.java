package com.freda.config.spring.xml;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * bean配置
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
		NamedNodeMap attrs = element.getAttributes();

		String id = element.getAttribute("id");
		if (id == null || id.length() <= 0) {
			String tempName = element.getAttribute("interface");
			id = tempName;
			int count = 2;
			while (parserContext.getRegistry().containsBeanDefinition(id)) {
				id = tempName + (count++);
			}
		}
		
		if (parserContext.getRegistry().containsBeanDefinition(id)) {
			throw new IllegalStateException("Duplicate spring bean id " + id);
		}
		parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
		beanDefinition.getPropertyValues().addPropertyValue("id", id);

		// set and get is a parameter
		for (Method setter : beanClass.getMethods()) {
			String setName = setter.getName();
			if (setName.length() > 3 && setName.startsWith("set") && Modifier.isPublic(setter.getModifiers())
					&& setter.getParameterTypes().length == 1) {
				String name = setName.substring(3, setName.length());
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
				if ("id".equals(property)) {
					continue;
				}
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
					} else {
						reference = value;
					}
					if (reference != null) {
						beanDefinition.getPropertyValues().addPropertyValue(property, reference);
					}
				}
			}
		}
		return beanDefinition;
	}

	private static String getPropertyName(String name) {
		return new StringBuilder().append(name.substring(0, 1).toLowerCase()).append(name.substring(1, name.length()))
				.toString();
	}
}
