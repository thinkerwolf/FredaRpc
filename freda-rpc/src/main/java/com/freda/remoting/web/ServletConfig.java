package com.freda.remoting.web;

import javax.servlet.Servlet;

public class ServletConfig {

	private Servlet servlet;

	private String name;

	private String mappingPath;

	public ServletConfig() {
	}

	public ServletConfig(Servlet servlet, String name, String mappingPath) {
		this.servlet = servlet;
		this.name = name;
		this.mappingPath = mappingPath;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public void setServlet(Servlet servlet) {
		this.servlet = servlet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMappingPath() {
		return mappingPath;
	}

	public void setMappingPath(String mappingPath) {
		this.mappingPath = mappingPath;
	}

}
