package com.freda.rpc.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.freda.common.conf.NetConfig;
import com.freda.remoting.web.ServletConfig;
import com.freda.remoting.web.WebServer;
import com.freda.remoting.web.tomcat.TomcatWebServerFactory;
import com.freda.rpc.AbstractProtocol;
import com.freda.rpc.Exporter;
import com.freda.rpc.Invoker;

public class HttpProtocol extends AbstractProtocol {

	public static final String NAME = "http";
	
	public static final String CONTEXT_PATH = "/services";
	
	private Map<Integer, WebServer> serverMap = new ConcurrentHashMap<>();

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public <T> Invoker<T> refer(String id, Class<T> type, List<NetConfig> ncs) {
		URL[] urls = new URL[ncs.size()];
		for (int i = 0; i < ncs.size(); i++) {
			try {
				urls[i] = new URL(ncs.get(i).getPath() + CONTEXT_PATH + "/" + id);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		HttpInvoker<T> invoker = new HttpInvoker<>(id, type, urls);
		return invoker;
	}

	@Override
	public <T> Exporter<T> export(String id, Class<T> type, T ref, NetConfig nc) {
		HttpExporter<T> exporter = new HttpExporter<T>(id, type, ref);
		getWebServer(nc);
		FrameworkServlet.getInstance().addExpoter(nc.getPort(), exporter);
		return exporter;
	}

	private WebServer getWebServer(NetConfig nc) {
		WebServer ws = serverMap.get(nc.getPort());
		if (ws == null) {
			TomcatWebServerFactory factory = new TomcatWebServerFactory();
			factory.setPort(nc.getPort());
			factory.addContextPath(CONTEXT_PATH);
			factory.addServlet(CONTEXT_PATH,
					new ServletConfig(FrameworkServlet.getInstance(), "frameworkServlet", "/*"));
			ws = factory.getWebServer();
			ws.start();
		}
		return ws;
	}

}
