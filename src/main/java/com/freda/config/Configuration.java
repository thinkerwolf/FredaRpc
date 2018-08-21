package com.freda.config;

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

import com.freda.common.conf.NettyConfig;
import com.freda.common.conf.RegistryConfig;
import com.freda.common.util.ReflectionUtils;
import com.freda.registry.Registry;
import com.freda.registry.ZooKeeperRegistry;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingServer;
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
	/** common netty server config list */
	private List<NettyConfig> nettyServerConfigs;
	/** common netty client config */
	private NettyConfig nettyClientConfig;
	/** common registry config list */
	private List<RegistryConfig> registryConfigs;
	/** Server map */
	private ConcurrentMap<NettyConfig, RemotingServer> remotingServerMap = new ConcurrentHashMap<>();
	/** Client map */
	private ConcurrentMap<NettyConfig, RemotingClient> remotingClientMap = new ConcurrentHashMap<>();
	/** Registry map */
	private ConcurrentMap<RegistryConfig, Registry> registryMap = new ConcurrentHashMap<>();
	/** Reference class client map */
	private Map<Class<?>, RemotingClient> exportRefRemoteMap = new ConcurrentHashMap<>();

	public void setNettyClientConfig(NettyConfig nettyClientConfig) {
		this.nettyClientConfig = nettyClientConfig;
	}

	public NettyConfig getNettyClientConfig() {
		return nettyClientConfig;
	}

	public List<NettyConfig> getNettyServerConfigs() {
		return nettyServerConfigs;
	}

	public void setNettyServerConfigs(List<NettyConfig> nettyServerConfigs) {
		this.nettyServerConfigs = nettyServerConfigs;
	}

	public List<RegistryConfig> getRegistryConfigs() {
		return registryConfigs;
	}

	public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
		this.registryConfigs = registryConfigs;
	}

	public void addRegistryConfig(RegistryConfig registryConfig) {
		if (this.registryConfigs == null) {
			this.registryConfigs = new ArrayList<>(1);
		}
		if (!this.registryConfigs.contains(registryConfig)) {
			this.registryConfigs.add(registryConfig);
		}
	}

	public void removeRegistry(Registry registry) {
		registryMap.remove(registry.getConf());
	}

	public RemotingClient getRefRemoting(Class<?> clazz) {
		return exportRefRemoteMap.get(clazz);
	}

	/**
	 * 
	 */
	public void addServiceConfig(ServiceConfig<?> sc) {
		List<Registry> registrys = new ArrayList<>();
		for (RegistryConfig rc : sc.getRegistryConfs()) {
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
		for (NettyConfig nc : sc.getNettyConfs()) {
			RemotingServer remoting = remotingServerMap.get(nc);
			if (remoting == null) {
				remoting = new NettyServer(nc);
				remoting.addRegistrys(registrys);
				remoting.start();
				remotingServerMap.put(nc, remoting);
			}
			remoting.addServiceConfig(sc);
		}
	}

	public void addReferenceConfig(ReferenceConfig<?> ref) {
		if (exportRefRemoteMap.get(ref.getInterfaceClass()) != null) {
			return;
		}
		List<Registry> registrys = new ArrayList<>();
		for (RegistryConfig rc : ref.getRegistryConfs()) {
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
		NettyConfig nc = ref.getNettyConf();
		RemotingClient remoting = remotingClientMap.get(nc);
		if (remoting == null) {
			remoting = new NettyClient(nc);
			remoting.addRegistrys(registrys);
			remoting.start();
			remotingClientMap.put(nc, remoting);
		}
		remoting.addReferenceConfig(ref);
		exportRefRemoteMap.put(ref.getInterfaceClass(), remoting);
	}
	
	
	RemotingClient getRemotingClient(NettyConfig nc) {
		return remotingClientMap.get(nc);
	}
	
	RemotingServer getRemotingServer(NettyConfig nc) {
		return remotingServerMap.get(nc);
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
		RegistryConfig registryConfig = new RegistryConfig();
		Map<String, InterfaceConfig<?>> icMap = new HashMap<String, InterfaceConfig<?>>();
		List<NettyConfig> nettyServerConfigs = new ArrayList<>();
		if (is != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			// 解析Netty-server 多个
			NodeList nettyServerNodeList = doc.getElementsByTagName("netty-server");
			for (int i = 0; i < nettyServerNodeList.getLength(); i++) {
				NettyConfig nettyConfig = new NettyConfig();
				Element nettyElement = (Element) nettyServerNodeList.item(i);
				if (nettyElement != null) {
					parsePropertyValue(nettyElement, "property", nettyConfig);
					nettyServerConfigs.add(nettyConfig);
				}
			}

			NettyConfig nettyClientConfig = new NettyConfig();
			// 解析Netty-client 一个
			Element nettyClientElement = (Element) doc.getElementsByTagName("netty-client").item(0);
			if (nettyClientElement != null) {
				parsePropertyValue(nettyClientElement, "property", nettyClientConfig);
			}
			configuration.setNettyClientConfig(nettyClientConfig);

			// 解析Registry
			Element registryElement = (Element) doc.getElementsByTagName("registry").item(0);
			if (registryElement != null) {
				parsePropertyValue(registryElement, "property", registryConfig);
			}

			// 解析service
			Node node = doc.getElementsByTagName("services").item(0);
			if (node != null) {
				parseServiceValue((Element) node, "service", icMap);
			}
			is.close();
		}
		configuration.addRegistryConfig(registryConfig);
		configuration.setNettyServerConfigs(nettyServerConfigs);
		for (InterfaceConfig<?> ic : icMap.values()) {
			ic.addRegistryConf(registryConfig);
			if (ic instanceof ServiceConfig) {
				((ServiceConfig<?>) ic).addNettyConfs(configuration.getNettyServerConfigs());
			} else {
				((ReferenceConfig<?>) ic).setNettyConf(configuration.getNettyClientConfig());
			}
			ic.setConf(configuration);
			ic.export();
		}
		return configuration;
	}

	@SuppressWarnings("unchecked")
	private static void parseServiceValue(Element element, String childTagName, Map<String, InterfaceConfig<?>> map) {
		NodeList serviceNodeList = element.getElementsByTagName(childTagName);
		for (int i = 0; i < serviceNodeList.getLength(); i++) {

			Element serviceElement = (Element) serviceNodeList.item(i);
			NamedNodeMap serviceAttrMap = serviceElement.getAttributes();

			Node idNode = serviceAttrMap.getNamedItem("id");
			Node classNode = serviceAttrMap.getNamedItem("class");
			Node interfaceNode = serviceAttrMap.getNamedItem("interface");

			if (interfaceNode == null) {
				continue;
			}

			InterfaceConfig inConfig = null;
			if (classNode != null) {
				inConfig = new ServiceConfig();
				Class<?> clazz = ReflectionUtils.getClassByName(classNode.getTextContent());
				inConfig.setRef(ReflectionUtils.newInstance(clazz));
			} else {
				inConfig = new ReferenceConfig();
			}

			Class<?> interfaceClass = ReflectionUtils.getClassByName(interfaceNode.getTextContent());
			inConfig.setInterfaceClass(interfaceClass);

			String id = idNode == null ? interfaceClass.getName() : idNode.getTextContent();
			if (map.get(id) != null) {
				throw new RuntimeException("duplicate name " + idNode.getTextContent());
			}
			inConfig.setId(id);

			if (classNode != null) {
				parsePropertyValue(serviceElement, "property", inConfig.getRef());
			}

			map.put(inConfig.getId(), inConfig);
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
				Object value = ReflectionUtils.getPrimitiveFieldValue(field, propertyValue);
				if (value != null) {
					field.set(obj, value);
				}
			} catch (Exception e) {
				logger.debug("propertyName not found", e);
				continue;
			}
		}
	}

}
