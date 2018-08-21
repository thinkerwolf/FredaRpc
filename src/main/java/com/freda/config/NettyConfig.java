package com.freda.config;

public class NettyConfig {

	private static final int DEFAULT_THREAD_NUM = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);

	private String ip;

	private int port;

	private String protocal = "freda";

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

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
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
		result = prime * result + ((protocal == null) ? 0 : protocal.hashCode());
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
		if (protocal == null) {
			if (other.protocal != null)
				return false;
		} else if (!protocal.equals(other.protocal))
			return false;
		return true;
	}

}
