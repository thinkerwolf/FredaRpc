package com.freda.common.conf;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.freda.common.util.ReflectionUtils;

/**
 * 项目配置 默认读取classpath下面的freda.xml文件
 * 
 * @author wukai
 *
 */
public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	/**
	 * netty配置
	 */
	private NettyConfig nettyConfig;

	/**
	 * 暴漏的Service配置
	 */
	private ConcurrentMap<String, ServiceConfig> serviceConfigMap = new ConcurrentHashMap<String, ServiceConfig>();

	/**
	 * 注册中心配置
	 */
	private RegistryConfig registryConfig;

	public NettyConfig getNettyConfig() {
		return nettyConfig;
	}

	public void setNettyConfig(NettyConfig nettyConfig) {
		this.nettyConfig = nettyConfig;
	}

	public ServiceConfig getServiceConfig(String id) {
		return serviceConfigMap.get(id);
	}

	public RegistryConfig getRegistryConfig() {
		return registryConfig;
	}

	public void setRegistryConfig(RegistryConfig registryConfig) {
		this.registryConfig = registryConfig;
	}

	public ServiceConfig getServiceConfig(Class<?> clazz) {
		for (ServiceConfig sc : serviceConfigMap.values()) {
			if (sc.getClazz() == clazz) {
				return sc;
			}
		}
		return null;
	}

	public static Configuration newConfiguration(String path) throws Exception {
		return newConfiguration(Configuration.class.getClassLoader().getResourceAsStream(path));
	}

	public static Configuration newConfiguration(InputStream is) throws Exception {
		if (is == null) {
			throw new IllegalArgumentException("null");
		}

		Configuration configuration = new Configuration();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);

		// 解析Netty
		NettyConfig nettyConfig = new NettyConfig();
		Element nettyElement = (Element) doc.getElementsByTagName("netty").item(0);
		if (nettyElement != null) {
			parsePropertyValue(nettyElement, "property", nettyConfig);
		}

		// 解析Registry
		RegistryConfig registryConfig = new RegistryConfig();
		Element registryElement = (Element) doc.getElementsByTagName("registry").item(0);
		if (registryElement != null) {
			parsePropertyValue(registryElement, "property", registryConfig);
		}

		// 解析service
		Node node = doc.getElementsByTagName("services").item(0);
		if (node != null) {
			parseServiceValue((Element) node, "service", configuration.serviceConfigMap);
		}

		configuration.setNettyConfig(nettyConfig);
		configuration.setRegistryConfig(registryConfig);
		return configuration;
	}

	private static void parseServiceValue(Element element, String childTagName, Map<String, ServiceConfig> map) {
		NodeList serviceNodeList = element.getElementsByTagName(childTagName);
		for (int i = 0; i < serviceNodeList.getLength(); i++) {
			ServiceConfig serviceConfig = new ServiceConfig();
			Element serviceElement = (Element) serviceNodeList.item(i);
			NamedNodeMap serviceAttrMap = serviceElement.getAttributes();

			Node idNode = serviceAttrMap.getNamedItem("id");
			Node classNode = serviceAttrMap.getNamedItem("class");
			Node interfaceNode = serviceAttrMap.getNamedItem("interface");

			if (map.get(idNode.getTextContent()) != null) {
				throw new RuntimeException("duplicate name " + idNode.getTextContent());
			}

			serviceConfig.setId(idNode.getTextContent());
			if (classNode != null) {
				Class<?> clazz = ReflectionUtils.getClassByName(classNode.getTextContent());
				serviceConfig.setServer(true);
				serviceConfig.setClazz(clazz);
				serviceConfig.setServiceObj(ReflectionUtils.newInstance(clazz));
			} else if (interfaceNode != null) {
				Class<?> clazz = ReflectionUtils.getClassByName(interfaceNode.getTextContent());
				serviceConfig.setServer(false);
				serviceConfig.setClazz(clazz);
			} else {
				throw new RuntimeException("lack class or interface attr");
			}
			if (serviceConfig.isServer()) {
				parsePropertyValue(serviceElement, "property", serviceConfig.getServiceObj());
			}
			map.put(serviceConfig.getId(), serviceConfig);
		}
	}

	private static void parsePropertyValue(Element element, String childTagName, Object obj) {
		NodeList propertyNodeList = element.getElementsByTagName(childTagName);
		Class<?> clazz = obj.getClass();
		for (int i = 0; i < propertyNodeList.getLength(); i++) {
			Node node = propertyNodeList.item(i);
			NamedNodeMap map = node.getAttributes();
			String propertyName = map.getNamedItem("name").getTextContent();
			String propertyValue = node.getTextContent();
			try {
				Field field = clazz.getDeclaredField(propertyName);
				field.setAccessible(true);
				Object value = getFieldPropertyValue(field, propertyValue);
				if (value != null) {
					field.set(obj, value);
				}
			} catch (Exception e) {
				logger.debug("propertyName not found", e);
				continue;
			}
		}
	}

	private static Object getFieldPropertyValue(Field field, String str) {
		Class<?> clazz = field.getType();
		if (clazz == String.class) {
			return str;
		} else if (clazz == byte.class || clazz == Byte.class) {
			return Byte.parseByte(str);
		} else if (clazz == short.class || clazz == Short.class) {
			return Short.parseShort(str);
		} else if (clazz == int.class || clazz == Integer.class) {
			return Integer.parseInt(str);
		} else if (clazz == long.class || clazz == Long.class) {
			return Long.parseLong(str);
		} else if (clazz == float.class || clazz == Float.class) {
			return Float.parseFloat(str);
		} else if (clazz == double.class || clazz == Double.class) {
			return Double.parseDouble(str);
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return Boolean.parseBoolean(str);
		}
		return null;
	}

}
