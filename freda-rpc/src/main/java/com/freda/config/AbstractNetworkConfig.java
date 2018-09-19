package com.freda.config;

import com.freda.common.Constants;
import com.freda.common.Net;

public class AbstractNetworkConfig extends AbstractConfig {

	private static final long serialVersionUID = 2550267656477169613L;

	protected String netframe;

	protected String protocol;

	protected String host;

	protected int port;

	protected String registries;

	protected String serialization = Constants.DEFAULT_SERIALIZATION;

	protected int timeout;

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

	public String getRegistries() {
		return registries;
	}

	public void setRegistries(String registries) {
		this.registries = registries;
	}

	public String getSerialization() {
		return serialization;
	}

	public void setSerialization(String serialization) {
		this.serialization = serialization;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public Net getNet() {
		Net n = new Net();
		n.setHost(host);
		n.setPort(port);
		n.setProtocol(protocol);
		if (serialization == null || serialization.length() == 0) {
			n.setSerialization(Constants.DEFAULT_SERIALIZATION);
		} else {
			n.setSerialization(serialization);
		}
		n.setTimeout(timeout);
		return n;
	}
	
}
