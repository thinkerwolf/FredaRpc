package com.freda.rpc.cluster;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClusterLoader {

	private static Map<String, Cluster> clusterMap = new HashMap<>();

	static {
		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
					.getResources("META-INF/freda.clusters");
			for (; urls.hasMoreElements();) {
				URL url = urls.nextElement();
				Properties pros = new Properties();
				pros.load(url.openStream());
				Enumeration<Object> keys = pros.keys();
				for (; keys.hasMoreElements();) {
					String key = String.valueOf(keys.nextElement()).trim();
					String clazzName = pros.getProperty(key).trim();
					if (clusterMap.get(key) != null) {
						throw new RuntimeException("Duplicate protocol name [" + key + "]");
					}

					try {
						Class<?> clazz = Class.forName(clazzName);
						if (!clazz.isInterface() && Cluster.class.isAssignableFrom(clazz)
								&& (clazz.getModifiers() & Modifier.ABSTRACT) == 0) {
							clusterMap.put(key, (Cluster) clazz.newInstance());
						}

					} catch (ClassNotFoundException e) {
						// Ingore
						continue;
					} catch (IllegalAccessException | InstantiationException e) {
						throw new RuntimeException(clazzName + " has no default construct");
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Cluster getClusterByName(String name) {
		return clusterMap.get(name);
	}

}
