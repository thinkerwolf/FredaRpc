package com.freda.common.conf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.freda.registry.Registry;
import com.freda.registry.ZooKeeperRegistry;
import com.freda.remoting.Remoting;
import com.freda.remoting.netty.NettyClient;
import com.freda.remoting.netty.NettyServer;

/**
 * 项目配置 默认读取classpath下面的freda.xml文件
 * 
 * @author wukai
 *
 */
@SuppressWarnings("rawtypes")
public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	/**
	 * 通用netty配置
	 */
	private List<NettyConfig> nettyConfigs;
	/**
	 * 通用注册中心配置
	 */
	private List<RegistryConfig> registryConfigs;
	/**
	 * 所有的远程配置
	 */
	private ConcurrentMap<NettyConfig, Remoting> remotingMap = new ConcurrentHashMap<>();
	/**
	 * 所有注册中心配置
	 */
	private ConcurrentMap<RegistryConfig, Registry> registryMap = new ConcurrentHashMap<>();

	public List<NettyConfig> getNettyConfigs() {
		return nettyConfigs;
	}

	public void addNettyConfig(NettyConfig nettyConfig) {
		if (this.nettyConfigs == null) {
			this.nettyConfigs = new ArrayList<>(1);
		}
		if (!this.nettyConfigs.contains(nettyConfig)) {
			this.nettyConfigs.add(nettyConfig);
		}
	}

	public void setNettyConfigs(List<NettyConfig> nettyConfigs) {
		this.nettyConfigs = nettyConfigs;
	}

	public List<RegistryConfig> getRegistryConfigs() {
		return registryConfigs;
	}

	public void addRegistryConfig(RegistryConfig registryConfig) {
		if (this.registryConfigs == null) {
			this.registryConfigs = new ArrayList<>(1);
		}
		if (!this.registryConfigs.contains(registryConfig)) {
			this.registryConfigs.add(registryConfig);
		}
	}

	public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
		this.registryConfigs = registryConfigs;
	}

	public void removeRegistry(Registry registry) {
		registryMap.remove(registry.getConf());
	}
	
	public List<Remoting> getRemotings() {
		return new ArrayList<>(remotingMap.values());
	}
	
	/**
	 * 
	 */
	public void addServiceConfig(ServiceConfig<?> sc) {
		List<RegistryConfig> rcs = sc.getRegistrys();
		List<NettyConfig> ncs = sc.getNetties();
		List<Registry> registrys = new ArrayList<>();
		for (RegistryConfig rc : rcs) {
			Registry registry = registryMap.get(rc);
			if (registry == null) {
				try {
					registry = new ZooKeeperRegistry(rc);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				registryMap.put(rc, registry);
			}
			registrys.add(registry);
		}
		for (NettyConfig nc : ncs) {
			Remoting remoting = remotingMap.get(nc);
			if (remoting == null) {
				if (sc.isServer()) {
					remoting = new NettyServer(nc);
				} else {
					remoting = new NettyClient(nc);
				}
				remoting.addRegistrys(registrys);
				remoting.start();
				remotingMap.put(nc, remoting);
			}
			remoting.addServiceConfig(sc);
		}
	}

	public static Configuration newConfiguration() throws Exception {
		InputStream is = Configuration.class.getClassLoader().getResourceAsStream("freda.xml");
		return newConfiguration(is);
	}

	public static Configuration newConfiguration(String path) throws Exception {
		if (path.startsWith("classpath:")) {
			return newConfiguration(
					Configuration.class.getClassLoader().getResourceAsStream(path.substring(10, path.length())));
		} else {
			return newConfiguration(new FileInputStream(path));
		}
	}

	public static Configuration newConfiguration(InputStream is) throws Exception {
		Configuration configuration = new Configuration();
		NettyConfig nettyConfig = new NettyConfig();
		RegistryConfig registryConfig = new RegistryConfig();
		Map<String, ServiceConfig> serviceMap = new HashMap<String, ServiceConfig>();
		if (is != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			// 解析Netty
			Element nettyElement = (Element) doc.getElementsByTagName("netty").item(0);

			if (nettyElement != null) {
				parsePropertyValue(nettyElement, "property", nettyConfig);
			}

			// 解析Registry
			Element registryElement = (Element) doc.getElementsByTagName("registry").item(0);
			if (registryElement != null) {
				parsePropertyValue(registryElement, "property", registryConfig);
			}

			// 解析service
			Node node = doc.getElementsByTagName("services").item(0);
			if (node != null) {
				parseServiceValue((Element) node, "service", serviceMap);
			}
			is.close();
		}
		configuration.addNettyConfig(nettyConfig);
		configuration.addRegistryConfig(registryConfig);
		for (ServiceConfig<?> sc : serviceMap.values()) {
			sc.addRegistry(registryConfig);
			sc.addNetty(nettyConfig);
			sc.setConf(configuration);
			sc.export();
		}
		return configuration;
	}

	@SuppressWarnings("unchecked")
	private static void parseServiceValue(Element element, String childTagName, Map<String, ServiceConfig> map) {
		NodeList serviceNodeList = element.getElementsByTagName(childTagName);
		for (int i = 0; i < serviceNodeList.getLength(); i++) {
			ServiceConfig serviceConfig = new ServiceConfig();
			Element serviceElement = (Element) serviceNodeList.item(i);
			NamedNodeMap serviceAttrMap = serviceElement.getAttributes();

			Node idNode = serviceAttrMap.getNamedItem("id");
			Node classNode = serviceAttrMap.getNamedItem("class");
			Node interfaceNode = serviceAttrMap.getNamedItem("interface");

			if (interfaceNode == null) {
				continue;
			}

			serviceConfig.setServer(false);
			if (classNode != null) {
				Class<?> clazz = ReflectionUtils.getClassByName(classNode.getTextContent());
				serviceConfig.setServer(true);
				serviceConfig.setRef(ReflectionUtils.newInstance(clazz));
			}

			Class<?> interfaceClass = ReflectionUtils.getClassByName(interfaceNode.getTextContent());
			serviceConfig.setInterfaceClass(interfaceClass);

			String id = idNode == null ? interfaceClass.getName() : idNode.getTextContent();
			if (map.get(id) != null) {
				throw new RuntimeException("duplicate name " + idNode.getTextContent());
			}
			serviceConfig.setId(id);

			if (serviceConfig.isServer()) {
				parsePropertyValue(serviceElement, "property", serviceConfig.getRef());
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
