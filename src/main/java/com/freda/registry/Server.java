package com.freda.registry;

import com.freda.util.JsonUtils;

/**
 * 服务器
 * 
 * @author wukai
 *
 */
public class Server {
	
	private String name;

	private String host;

	private int port;

	public Server(String name, String host, int port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public byte[] toJsonByte() {
		return JsonUtils.obj2Json(this).getBytes();
	}

	public static void main(String[] args) {
		Server server = new Server("freda_1", "127.0.0.1", 11211);
		System.out.println(JsonUtils.obj2Json(server));
	}

}
