package com.freda.remoting.web.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import com.freda.remoting.web.WebServer;
import com.freda.remoting.web.WebServerException;

public class TomcatWebServer implements WebServer {

	private Tomcat tomcat;

	public TomcatWebServer(Tomcat tomcat) {
		this.tomcat = tomcat;
	}

	@Override
	public void start() throws WebServerException {
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new WebServerException("tomcat server start fail", e);
		}
	}

	@Override
	public void stop() throws WebServerException {
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			throw new WebServerException("tomcat server stop fail", e);
		}
	}

}
