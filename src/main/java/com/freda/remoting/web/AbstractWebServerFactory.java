package com.freda.remoting.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;

public abstract class AbstractWebServerFactory implements WebServerFactory {
	/** port */
	private int port;
	/** <contextPath, <servletName, Servlet>> */
	private Map<String, Map<String, Servlet>> servletMap = new HashMap<>();
	/** all contextPaths */
	private Set<String> contextPaths = new HashSet<String>();
	
	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	public synchronized void addServlet(String contextPath, String name, Servlet servlet) {
		Map<String, Servlet> map = servletMap.get(contextPath);
		if (map == null) {
			map = new HashMap<>();
			servletMap.put(contextPath, map);
		}
		if (!contextPaths.contains(contextPath)) {
			contextPaths.add(contextPath);
		}
		map.put(name, servlet);
	}

	public synchronized void addContextPath(String contextPath) {
		contextPaths.add(contextPath);
	}

}
