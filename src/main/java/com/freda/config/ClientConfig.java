package com.freda.config;


import org.apache.commons.lang3.StringUtils;

/**
 * client config
 * @author wukai
 *
 */
public class ClientConfig {
	protected String id;
	/**  */
	protected String netframe;
	
	protected String protocol;
	
	protected String host;
	
	protected int port;
	
	protected String registries;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNetframe() {
		return netframe;
	}

	public void setNetframe(String netframe) {
		this.netframe = netframe;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean isUsable() {
		if (StringUtils.isNotEmpty(host) && port > 0 && StringUtils.isNotEmpty(protocol)) {
			return true;
		}
		return false;
	}
	
	
	
	
	
}