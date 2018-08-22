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

import com.freda.registry.Server;
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
import com.freda.remoting.RemotingFactory;
import com.freda.remoting.RemotingServer;
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

	public RemotingClient addReferenceConfig(ReferenceConfig<?> ref) {
		if (exportRefRemoteMap.get(ref.getInterfaceClass()) != null) {
			return null;
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
		if (registrys.size() <= 0) {
		    throw new RuntimeException("registry num == 0");
        }

		NettyConfig nc = ref.getNettyConfig();
		RemotingClient remoting = null;
		if (nc.isUseable()) {
			remoting = remotingClientMap.get(nc);
			if (remoting == null) {
				remoting = RemotingFactory.getInstance().createRemotingClient(nc, registrys);
				remoting.start();
				remotingClientMap.put(nc, remoting);
			}
		} else {
			 NettyConfig newNc = nc.clone();
			 try {
	                Server server = registrys.get(0).getRandomServer(newNc.getProtocal());
	                if (server == null) {
	                    return null;
	                }
	                newNc.setIp(server.getHost());
	                newNc.setPort(server.getPort());
	                ref.setNettyConf(newNc);
	            } catch (Exception e) {
	                e.printStackTrace();
	                return null;
	            }
	            remoting = remotingClientMap.get(newNc);
	            if (remoting == null) {
	                remoting = RemotingFactory.getInstance().createRemotingClient(newNc, registrys);
	                remoting.start();
	                remotingClientMap.put(newNc, remoting);
	            }
		}
		remoting.addReferenceConfig(ref);
		exportRefRemoteMap.put(ref.getInterfaceClass(), remoting);
		return remoting;
	}

	RemotingClient getRemotingClient(NettyConfig nc) {
		return remotingClientMap.get(nc);
	}

	RemotingServer getRemotingServer(NettyConfig nc) {
		return remotingServerMap.get(nc);
	}


	private static Configuration INSTANCE;
	public static Configuration getInstance() {
		if (INSTANCE == null) {
			synchronized (Configuration.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = newConfiguration();
					} catch (Exception e) {
						//e.printStackTrace();
						INSTANCE = new Configuration();
					}
				}
			}
		}
		return INSTANCE;
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
		Map<String, InterfaceConfig<?>> serviceMap = new HashMap<>();
		Map<String, InterfaceConfig<?>> referenceMap = new HashMap<>();
		List<NettyConfig> nettyServerConfigs = new ArrayList<>();
		NettyConfig nettyClientConfig = new NettyConfig();
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
			Node serviceNode = doc.getElementsByTagName("services").item(0);
			if (serviceNode != null) {
				parseServiceValue((Element) serviceNode, "service", serviceMap);
			}

			// 解析reference
			Node referenceNode = doc.getElementsByTagName("references").item(0);
			if (referenceNode != null) {
				parseReferenceValue((Element) referenceNode, "reference", referenceMap);
			}

			is.close();
		}
		configuration.setNettyClientConfig(nettyClientConfig);
		configuration.addRegistryConfig(registryConfig);
		configuration.setNettyServerConfigs(nettyServerConfigs);

		for (InterfaceConfig<?> ic : serviceMap.values()) {
			ic.addRegistryConf(registryConfig);
			((ServiceConfig<?>) ic).addNettyConfs(configuration.getNettyServerConfigs());
			ic.setConf(configuration);
			ic.export();
		}

		for (InterfaceConfig<?> ic : referenceMap.values()) {
			ic.addRegistryConf(registryConfig);
			((ReferenceConfig<?>) ic).setNettyConf(configuration.getNettyClientConfig());
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

			ServiceConfig sc = new ServiceConfig();
			Class<?> clazz = ReflectionUtils.getClassByName(classNode.getTextContent());
			sc.setRef(ReflectionUtils.newInstance(clazz));

			sc.setInterface(interfaceNode.getTextContent());

			String id = idNode == null ? interfaceNode.getTextContent() : idNode.getTextContent();
			if (map.get(id) != null) {
				throw new RuntimeException("duplicate name " + idNode.getTextContent());
			}
			sc.setId(id);
			parsePropertyValue(serviceElement, "property", sc.getRef());
			map.put(sc.getId(), sc);
		}
	}

	private static void parseReferenceValue(Element element, String childTagName, Map<String, InterfaceConfig<?>> map) {
		NodeList serviceNodeList = element.getElementsByTagName(childTagName);
		for (int i = 0; i < serviceNodeList.getLength(); i++) {
			Element serviceElement = (Element) serviceNodeList.item(i);
			NamedNodeMap serviceAttrMap = serviceElement.getAttributes();

			Node idNode = serviceAttrMap.getNamedItem("id");
			Node interfaceNode = serviceAttrMap.getNamedItem("interface");
			if (interfaceNode == null) {
				continue;
			}
			ReferenceConfig rc = new ReferenceConfig();
			Class<?> interfaceClass = ReflectionUtils.getClassByName(interfaceNode.getTextContent());
			rc.setInterface(interfaceNode.getTextContent());
			String id = idNode == null ? interfaceClass.getName() : idNode.getTextContent();
			if (map.get(id) != null) {
				throw new RuntimeException("duplicate name " + idNode.getTextContent());
			}
			rc.setId(id);
			map.put(rc.getId(), rc);
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
