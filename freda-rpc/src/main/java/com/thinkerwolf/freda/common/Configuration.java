package com.thinkerwolf.freda.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Freda configuration, Default load <strong>freda.properities</strong> file in
 * classpath
 *
 */
public class Configuration {
	/** Default configuration path */
	public static String configPath = "freda.properties";
	/** Netty boss thread num */
	public static final String NETTY_BOSS_THREADS = "netty.boss.threads";
	/** Netty worker thread num */
	public static final String NETTY_WORKER_THREADS = "netty.worker.threads";
	/** Enable jmx monitor */
	public static final String JMX_MONITOR_ENABLED = "jmx.monitor.enabled";

	private static Map<String, String> props = new ConcurrentHashMap<>();

	static {
		loadProps();
	}

	private static void loadProps() {
		String path = Constants.CLASS_PATH + configPath;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fis);
				for (Map.Entry<Object, Object> en : properties.entrySet()) {
					props.put(en.getKey().toString(), en.getValue().toString());
				}
			} catch (Exception e) {
				// Ignore
			}
		}

	}

	public static int getIntProperty(String name, int defaultValue) {
		String s = props.get(name);
		if (StringUtils.isNotEmpty(s)) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {

			}
		}
		return defaultValue;
	}

	public static String getStringProperty(String name) {
		return props.get(name);
	}

	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		String s = props.get(name);
		if (StringUtils.isNotEmpty(s)) {
			try {
				return Boolean.parseBoolean(s);
			} catch (Exception e) {

			}
		}
		return defaultValue;

	}

}
