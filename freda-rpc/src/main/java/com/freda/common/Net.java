package com.freda.common;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Net {

	private String host;

	private int port = -1;

	private String protocol = "freda";

	private String serialization = "hessian2";

	private int timeout;

	private Map<String, Object> parameters;

	public Net() {

	}

	public Net(String host, int port, String protocol) {
		this(host, port, protocol, Constants.DEFAULT_TIMEOUT);
	}

	public Net(String host, int port, String protocol, int timeout) {
		this(host, port, protocol, timeout, Constants.DEFAULT_SERIALIZATION);
	}

	public Net(String host, int port, String protocol, int timeout, String serialization) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.timeout = timeout;
		this.serialization = serialization;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getSerialization() {
		return serialization;
	}

	public void setSerialization(String serialization) {
		this.serialization = serialization;
	}

	public boolean isUseable() {
		if (StringUtils.isNotEmpty(host) && port > 0 && StringUtils.isNotEmpty(protocol)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Net [host=" + host + ", port=" + port + ", protocol=" + protocol + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
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
		Net other = (Net) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
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
	public Net clone() {
		Net n = new Net();
		n.setHost(this.host);
		n.setPort(this.port);
		n.setProtocol(this.protocol);
		n.setTimeout(this.timeout);
		n.setSerialization(this.serialization);
		return n;
	}

	public URI uri() {
		// http://java.sun.com/j2se/1.3/
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":" + port);
		try {
			return new URI(sb.toString());
		} catch (URISyntaxException e) {
			throw new RuntimeException("netty parse to uri fail", e);
		}
	}

	public String getPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":" + port);
		return sb.toString();
	}

	public String key() {
		return port > 0 ? host + ":" + port : host;
	}

	public Map<String, Object> getParameters() {
		if (parameters == null) {
			synchronized (this) {
				if (parameters == null) {
					parameters = new ConcurrentHashMap<>();
				}
			}
		}
		return parameters;
	}

	public void putParameter(String key, Object value) {
		Map<String, Object> parameters = getParameters();
		parameters.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String key, T defaultValue) {
		Object value = getParameters().get(key);
		if (value == null) {
			getParameters().put(key, defaultValue);
		}
		return value == null ? defaultValue : (T) value;
	}

}
