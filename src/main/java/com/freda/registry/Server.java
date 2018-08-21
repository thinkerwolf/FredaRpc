package com.freda.registry;

import com.freda.common.util.JsonUtils;

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

	private String protocal;

	public Server() {
	}

	public Server(String name, String host, int port, String protocal) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.protocal = protocal;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
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

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public byte[] toJsonByte() {
		return JsonUtils.obj2Json(this).getBytes();
	}

	public static void main(String[] args) {
		Server server = new Server("freda_1", "127.0.0.1", 11211, "freda");

		String json = JsonUtils.obj2Json(server);
		System.out.println(json);

		Server s = JsonUtils.json2Obj(json, Server.class);
		System.out.println(s.getPort());

	}

}
