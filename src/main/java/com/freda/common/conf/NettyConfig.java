package com.freda.common.conf;

public class NettyConfig {

	private static final int DEFAULT_THREAD_NUM = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);

	private String ip = "127.0.0.1";

	private int port = 11211;

	private String protocal = "freda";

	private int bossThreads = 2;

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
	
}
