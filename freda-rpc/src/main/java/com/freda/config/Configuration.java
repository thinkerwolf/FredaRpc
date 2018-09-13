package com.freda.config;

import com.freda.common.Net;
import com.freda.common.ServiceLoader;
import com.freda.common.util.ReflectionUtils;
import com.freda.registry.Registry;
import com.freda.registry.RegistryFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 项目配置 默认读取classpath下面的freda.xml文件
 *
 * @author wukai
 */
@SuppressWarnings("rawtypes")
public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static Configuration INSTANCE;
	/**
	 * common netty server config map
	 */
	Map<String, ServerConfig> serverConfigMap;
	/**
	 * common netty client config map
	 */
	Map<String, ClientConfig> clientConfigMap;
	/**
	 * common registry config list
	 */
	private List<RegistryConfig> registryConfigs;
	
	/**
	 * Server map
	private ConcurrentMap<String, RemotingServer> remotingServerMap = new ConcurrentHashMap<>(); */
	/**
	 * Client map
	private ConcurrentMap<String, RemotingClient> remotingClientMap = new ConcurrentHashMap<>();  */
	/**
	 * Reference class client map
	private Map<Class<?>, RemotingClient> exportRefRemoteMap = new ConcurrentHashMap<>(); */
	
	private ConcurrentMap<String, ReferenceConfig<?>> referenceConfMap = new ConcurrentHashMap<>();
	
	private ConcurrentMap<String, ServiceConfig<?>> serviceConfMap = new ConcurrentHashMap<>();
	
	/**
	 * Registry map
	 */
	private ConcurrentMap<String, Registry> registryMap = new ConcurrentHashMap<>();
	

	public static Configuration getInstance() {
		if (INSTANCE == null) {
			synchronized (Configuration.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = newConfiguration();
					} catch (Exception e) {
						// e.printStackTrace();
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
		Map<String, ServerConfig> serverConfigMap = new HashMap<>();
		Map<String, ClientConfig> clientConfigMap = new HashMap<>();
		if (is != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			// 解析server
			NodeList serverNodeList = doc.getElementsByTagName("server");
			for (int i = 0; i < serverNodeList.getLength(); i++) {
				ServerConfig sc = new ServerConfig();
				Element nettyElement = (Element) serverNodeList.item(i);
				if (nettyElement != null) {
					parsePropertyValue(nettyElement, "property", sc);
					serverConfigMap.put(sc.getId(), sc);
				}
			}

			// 解析 client
			NodeList clientNodeList = doc.getElementsByTagName("client");
			for (int i = 0; i < clientNodeList.getLength(); i++) {
				ClientConfig cc = new ClientConfig();
				Element nettyElement = (Element) serverNodeList.item(i);
				if (nettyElement != null) {
					parsePropertyValue(nettyElement, "property", cc);
					clientConfigMap.put(cc.getId(), cc);
				}
			}

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
		configuration.setServerConfigMap(serverConfigMap);
		configuration.setClientConfigMap(clientConfigMap);
		configuration.addRegistryConfig(registryConfig);
		
		for (InterfaceConfig<?> ic : serviceMap.values()) {
			ic.addRegistryConf(registryConfig);
			ServiceConfig<?> sc = (ServiceConfig<?>) ic;
			List<ServerConfig> confs = new LinkedList<>();
			if (StringUtils.isNotEmpty(sc.getServers())) {
				for (String cId : sc.getServers().split(",")) {
					ServerConfig ss = serverConfigMap.get(cId);
					if (sc != null) {
						confs.add(ss);
					}
				}
			} else {
				for (ServerConfig ss : serverConfigMap.values()) {
					confs.add(ss);
				}
			}
			sc.setServerConfigs(confs);
			ic.setConf(configuration);
			ic.export();
			configuration.addServiceConf((ServiceConfig<?>) ic);
		}

		for (InterfaceConfig<?> ic : referenceMap.values()) {
			ic.addRegistryConf(registryConfig);
			ReferenceConfig<?> rc = (ReferenceConfig<?>) ic;
			List<ClientConfig> clientConfs = new LinkedList<>();
			if (StringUtils.isNotEmpty(rc.getClients())) {
				for (String cId : rc.getClients().split(",")) {
					ClientConfig cc = clientConfigMap.get(cId);
					if (cc != null) {
						clientConfs.add(cc);
					}
				}
			} else {
				for (ClientConfig cc : clientConfigMap.values()) {
					clientConfs.add(cc);
				}
			}
			rc.setClientConfigs(clientConfs);
			
			ic.setConf(configuration);
			ic.export();
			configuration.addReferenceConf((ReferenceConfig<?>) ic);
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
	
	public Map<String, ServerConfig> getServerConfigMap() {
		return serverConfigMap;
	}

	public void setServerConfigMap(Map<String, ServerConfig> serverConfigMap) {
		this.serverConfigMap = serverConfigMap;
	}

	public Map<String, ClientConfig> getClientConfigMap() {
		return clientConfigMap;
	}

	public void setClientConfigMap(Map<String, ClientConfig> clientConfigMap) {
		this.clientConfigMap = clientConfigMap;
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
		registryMap.remove(registry.getNet().key());
	}
	
	public void addReferenceConf(ReferenceConfig<?> rc) {
		referenceConfMap.put(rc.interfaceName, rc);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ReferenceConfig<T> getReferenceConf(Class<T> clazz) {
		return (ReferenceConfig<T>) referenceConfMap.get(clazz.getName());
	}
	
	public void addServiceConf(ServiceConfig<?> sc) {
		serviceConfMap.put(sc.interfaceName, sc);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ServiceConfig<T> getServiceConf(Class<T> clazz) {
		return (ServiceConfig<T>) serviceConfMap.get(clazz.getName());
	}
	
	/**
	public RemotingClient getRefRemoting(Class<?> clazz) {
		return exportRefRemoteMap.get(clazz);
	}
	*/

	/**
	 *
	public void addServiceConfig(ServiceConfig<?> sc) {
		List<Registry> registries = handleRegistries(sc.getRegistryConfs());
		for (NetConfig nc : sc.getNettyConfs()) {
			RemotingServer remoting = remotingServerMap.get(nc.key());
			if (remoting == null) {
				remoting = RemotingFactory.getInstance().createRemotingServer(nc, registries);
				remoting.addRegistrys(registries);
				remoting.start();
				remotingServerMap.put(nc.key(), remoting);
			}
			remoting.handler().addServiceConfig(sc);
		}
	}
	public RemotingClient addReferenceConfig(ReferenceConfig<?> ref) {
		if (exportRefRemoteMap.get(ref.getInterfaceClass()) != null) {
			return null;
		}
		List<Registry> registries = handleRegistries(ref.getRegistryConfs());
		NetConfig nc = ref.getNetConfig();
		if (registries.size() <= 0 && !nc.isUseable()) {
			throw new RuntimeException("can't export [" + ref.getInterface()
					+ "], because there's no registry config and nettyConfig can't be used");
		}
		RemotingClient remoting = null;
		if (nc.isUseable()) {
			remoting = remotingClientMap.get(nc.key());
			if (remoting == null) {
				remoting = RemotingFactory.getInstance().createRemotingClient(nc, registries);
				remoting.start();
				remotingClientMap.put(nc.key(), remoting);
			}
		} else {
			NetConfig newNc = nc.clone();
			try {
				Server server = registries.get(0).getRandomServer(newNc.getProtocol());
				if (server == null) {
					return null;
				}
				newNc.setIp(server.getHost());
				newNc.setPort(server.getPort());
				ref.setNetConf(newNc);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			remoting = remotingClientMap.get(newNc.key());
			if (remoting == null) {
				remoting = RemotingFactory.getInstance().createRemotingClient(newNc, registries);
				remoting.start();
				remotingClientMap.put(newNc.key(), remoting);
			}
		}
		remoting.handler().addReferenceConfig(ref);
		exportRefRemoteMap.put(ref.getInterfaceClass(), remoting);
		return remoting;
	}
	*/

	public List<Registry> handleRegistries(Collection<RegistryConfig> set) {
		List<Registry> registries = new ArrayList<>();
		for (RegistryConfig rc : set) {
			Registry registry = registryMap.get(rc.key());
			if (registry == null) {
				RegistryFactory rf = ServiceLoader.getService(rc.getProtocol(), RegistryFactory.class);
				try {
					registry = rf.getRegistry(new Net(rc.getHost(), rc.getPort(), rc.getProtocol(), rc.getTimeout()));
					registry.start();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				registryMap.put(rc.key(), registry);
			}
			registries.add(registry);
		}
		return registries;
	}

	/**
	RemotingClient getRemotingClient(NetConfig nc) {
		return remotingClientMap.get(nc);
	}
	RemotingServer getRemotingServer(NetConfig nc) {
		return remotingServerMap.get(nc);
	}
	*/

}
