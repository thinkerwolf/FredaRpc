package com.freda.common.conf;

import org.apache.commons.lang3.StringUtils;

public class NettyConfig {

	private static final int DEFAULT_THREAD_NUM = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);

	private String ip;

	private int port;

	private String protocol = "freda";

	private int bossThreads = Math.max(1, DEFAULT_THREAD_NUM / 2);

	private int workerThreads = DEFAULT_THREAD_NUM;

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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getBossThreads() {
		return bossThreads;
	}

	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}
	
	public boolean isUseable() {
		if (StringUtils.isNotEmpty(ip) && port > 0 && StringUtils.isNotEmpty(protocol)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "host#" + ip + ", port#" + port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NettyConfig other = (NettyConfig) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}

	@Override
	public NettyConfig clone() {
		NettyConfig nettyConfig = new NettyConfig();
		nettyConfig.setIp(this.ip);
		nettyConfig.setPort(this.port);
		nettyConfig.setProtocol(this.protocol);
		nettyConfig.setBossThreads(this.bossThreads);
		nettyConfig.setWorkerThreads(this.workerThreads);
		return nettyConfig;
	}



}
