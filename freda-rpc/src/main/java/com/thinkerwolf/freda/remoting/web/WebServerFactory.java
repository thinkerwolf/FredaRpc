package com.thinkerwolf.freda.remoting.web;


public interface WebServerFactory {

    WebServer getWebServer();

    int getPort();

    void setPort(int port);

    void addServlet(String contextPath, ServletConfig servlet);

    void setBaseDir(String baseDir);

}
