package com.freda.common.conf;

/**
 * Service配置
 * 
 * @author wukai
 *
 */
public class ServiceConfig {
	/**
	 * Service实体
	 */
	private Object serviceObj;
	/**
	 * Service id
	 */
	private String id;
	/**
	 * class
	 */
	private Class<?> clazz;

	private boolean server;

	public Object getServiceObj() {
		return serviceObj;
	}

	public void setServiceObj(Object serviceObj) {
		this.serviceObj = serviceObj;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "id#" + id + ", clazz#" + clazz;
	}

}
