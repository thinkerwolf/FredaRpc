package com.freda.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * 内嵌Tomcat测试
 * 
 * @author wukai
 *
 */
public class EmbedTomcatServer {
	
	public static void main(String[] args) throws LifecycleException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		
		System.out.println(url.getPath());
		
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.addContext(File.separator + "freda", url.getPath() + File.separator + "webapps");
		tomcat.addServlet(File.separator + "freda", "dispatch", new InnerServlet());
		tomcat.getConnector().setURIEncoding("UTF-8");
		tomcat.start();
		tomcat.getServer().await();
	}
	
	
	static class InnerServlet extends HttpServlet {
		@Override
		protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			//super.service(req, resp);
			
			req.getProtocol();
			
			
		}
		
	}
	
}





