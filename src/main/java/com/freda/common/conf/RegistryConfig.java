package com.freda.common.conf;

public class RegistryConfig {

	private String ip;

	private int port;

	private int timeout = 1000;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getConnAddress() {
		return ip + ":" + port;
	}

}
