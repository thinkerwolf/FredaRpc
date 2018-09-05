package com.freda.rpc;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProtocolLoader {

	private static Map<String, Protocol> protocolMap = new HashMap<>();
	
	static {
		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
					.getResources("META-INF/freda.protocols");
			for (; urls.hasMoreElements();) {
				URL url = urls.nextElement();
				Properties pros = new Properties();
				pros.load(url.openStream());
				Enumeration<Object> keys = pros.keys();
				for (; keys.hasMoreElements();) {
					String key = String.valueOf(keys.nextElement()).trim();
					String protocolClazz = pros.getProperty(key).trim();
					if (protocolMap.get(key) != null) {
						throw new RuntimeException("Duplicate protocol name [" + key + "]");
					}

					try {
						Class<?> clazz = Class.forName(protocolClazz);
						if (!clazz.isInterface() && Protocol.class.isAssignableFrom(clazz)
								&& (clazz.getModifiers() & Modifier.ABSTRACT) == 0) {
							protocolMap.put(key, (Protocol) clazz.newInstance());
						}

					} catch (ClassNotFoundException e) {
						// Ingore
						continue;
					} catch (IllegalAccessException | InstantiationException e) {
						throw new RuntimeException(protocolClazz + " has no default construct");
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Protocol getProtocolByName(String name) {
		return protocolMap.get(name);
	}

	public static void main(String[] args) {
		System.out.println(ProtocolLoader.getProtocolByName("freda"));
	}

}
