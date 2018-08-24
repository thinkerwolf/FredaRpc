package com.freda.config.spring.xml;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.freda.config.spring.NetBean;

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

		// generate bean id and register
		String beanId = element.getAttribute("id");
		if (!StringUtils.hasText(beanId)) {
			String generateName = element.getAttribute("interface");
			if (!StringUtils.hasText(generateName)) {
				generateName = beanClass.getName();
			}
			int count = 2;
			beanId = generateName + count;
			while (parserContext.getRegistry().containsBeanDefinition(beanId)) {
				beanId = generateName + (count++);
			}
		}
		if (parserContext.getRegistry().containsBeanDefinition(beanId)) {
			throw new IllegalStateException("Duplicate spring bean id " + beanId);
		}
		parserContext.getRegistry().registerBeanDefinition(beanId, beanDefinition);

		// set and get is a parameter
		for (Method setter : beanClass.getMethods()) {
			String setName = setter.getName();
			if (setName.length() > 3 && setName.startsWith("set") && Modifier.isPublic(setter.getModifiers())
					&& setter.getParameterTypes().length == 1) {
				String name = setName.substring(3, setName.length());
				// Method getter = null;
				try {
					// getter =
					beanClass.getMethod("get" + name);
				} catch (Exception e) {
					try {
						// getter =
						beanClass.getMethod("is" + name);
					} catch (Exception e1) {
						continue;
					}
				}
				String property = getPropertyName(name);
				
				if ("id".equals(property)) {
					String id = element.getAttribute("id");
					if (!StringUtils.hasText(id)) {
						id = element.getAttribute("ref");
					}
					if (!StringUtils.hasText(id)) {
						id = element.getAttribute("interface");
					}
					if (!StringUtils.hasText(id)) {
						beanDefinition.getPropertyValues().addPropertyValue("id", beanId);
					} else {
						beanDefinition.getPropertyValues().addPropertyValue("id", id);
					}
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
		
		if (beanClass.equals(NetBean.class)) {
			String tag = element.getTagName();
			if (tag.endsWith("netty-server")) {
				beanDefinition.getPropertyValues().addPropertyValue("server", true);
			} else {
				beanDefinition.getPropertyValues().addPropertyValue("server", false);
			}
		}
		
		return beanDefinition;
	}

	private static String getPropertyName(String name) {
		return new StringBuilder().append(name.substring(0, 1).toLowerCase()).append(name.substring(1, name.length()))
				.toString();
	}
}
