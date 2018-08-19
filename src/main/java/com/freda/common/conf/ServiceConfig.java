package com.freda.common.conf;

/**
 * Service配置
 * 
 * @author wukai
 *
 */
public class ServiceConfig<T> {
	/**
	 * Service对应的实体
	 */
	private T ref;
	/**
	 * Service id
	 */
	private String id;
	/**
	 * class
	 */
	private Class<T> interfaceClass;

	private boolean server;

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "id#" + id + ", clazz#" + interfaceClass;
	}

	/**
	 * 加入暴漏列表中
	 */
	public void doExport() {

	}

}
