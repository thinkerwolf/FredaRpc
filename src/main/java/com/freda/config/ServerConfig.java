package com.freda.config;

public class ServerConfig {

	protected String id;

	protected String netframe;

	protected String protocol;

	protected String host;

	protected int port;

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

}
