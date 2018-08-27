package com.freda.remoting.web.tomcat;

import com.freda.remoting.web.FredaDispatchServlet;
import com.freda.remoting.web.WebServer;
import com.freda.remoting.web.WebServerException;
import com.freda.remoting.web.WebServerFactory;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;

import com.freda.remoting.web.AbstractWebServerFactory;
import com.freda.remoting.web.WebServer;
import com.freda.remoting.web.WebServerException;
import java.io.File;
import java.io.IOException;

public class TomcatWebServerFactory extends AbstractWebServerFactory {

	private int port = 8080;
	private String docBase;

	@Override
	public WebServer getWebServer() {
		File file = docBase == null ? createTempDir("tomcat") : new File(docBase);
		if (file.isDirectory() && file.exists()) {
			file.mkdirs();
		}
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		tomcat.setBaseDir(file.getAbsolutePath());
		tomcat.getHost().setAutoDeploy(false);

		String contextPath = "/freda";
		StandardContext context = new StandardContext();
		context.setPath(contextPath);
		context.addLifecycleListener(new FixContextListener());
		tomcat.getHost().addChild(context);

		// tomcat.addServlet(contextPath, "InternalServlet", new
		// InnerServlet());
		tomcat.addServlet("/", "dispatchServlet", new FredaDispatchServlet());
		context.addServletMappingDecoded("/service", "dispatchServlet");

		return getTomcatWebServer(tomcat);
	}

	/**
	 * Return the absolute temp dir for given web server.
	 *
	 * @param prefix
	 *            server name
	 * @return The temp dir for given server.
	 */
	protected final File createTempDir(String prefix) {
		try {
			File tempDir = File.createTempFile(prefix + ".", "." + getPort());
			tempDir.delete();
			tempDir.mkdir();
			tempDir.deleteOnExit();
			return tempDir;
		} catch (IOException ex) {
			throw new WebServerException(
					"Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
		}
	}

	private TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
		return new TomcatWebServer(tomcat);
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

}
