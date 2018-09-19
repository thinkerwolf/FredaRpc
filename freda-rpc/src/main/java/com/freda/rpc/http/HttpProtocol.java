package com.freda.rpc.http;

import com.freda.common.Net;
import com.freda.remoting.web.ServletConfig;
import com.freda.remoting.web.WebServer;
import com.freda.remoting.web.tomcat.TomcatWebServerFactory;
import com.freda.rpc.AbstractProtocol;
import com.freda.rpc.Exporter;
import com.freda.rpc.Invoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpProtocol extends AbstractProtocol {

    public static final String NAME = "http";

    public static final String CONTEXT_PATH = "/services";

    private Map<Integer, WebServer> serverMap = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public <T> Invoker<T> refer(String id, Class<T> type, List<Net> ncs) {
        Net[] nets = new Net[ncs.size()];
        ncs.toArray(nets);
        HttpInvoker<T> invoker = new HttpInvoker<>(id, type, nets);
        return invoker;
    }

    @Override
    public <T> Exporter<T> export(String id, Class<T> type, T ref, Net nc) {
        HttpExporter<T> exporter = new HttpExporter<T>(id, type, ref);
        getWebServer(nc);
        FrameworkServlet.getInstance().addExpoter(nc.getPort(), exporter);
        return exporter;
    }

    private WebServer getWebServer(Net nc) {
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
