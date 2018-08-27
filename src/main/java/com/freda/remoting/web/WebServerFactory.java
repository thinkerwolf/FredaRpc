package com.freda.remoting.web;

import javax.servlet.Servlet;

public interface WebServerFactory {

    WebServer getWebServer();

    int getPort();

    void setPort(int port);

	void addServlet(String contextPath, String name, Servlet servlet);
	
}
