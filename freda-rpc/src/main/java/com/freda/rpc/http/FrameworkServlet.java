package com.freda.rpc.http;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Exporter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Freda Http请求Servlet
 *
 * @author wukai
 */
public class FrameworkServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static FrameworkServlet INSTANCE = new FrameworkServlet();
    private Map<Integer, Map<String, Exporter<?>>> exporters = new ConcurrentHashMap<>();

    public static FrameworkServlet getInstance() {
        return INSTANCE;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("POST".equals(req.getMethod())) {
            Exporter<?> exporter = getExpoter(req.getLocalPort(), req.getRequestURI());
            if (exporter == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Service not found");
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream());
            try {

                RequestMessage requestMessage = (RequestMessage) ois.readObject();
                Object result = exporter.invoke(requestMessage.getMethodName(), requestMessage.getParameterTypes(), requestMessage.getArgs());

                // test
                // Object result = exporter.invoke("sayHello", new Class<?>[] { String.class }, new Object[]{"freda http"});
                resp.setContentType("text/json");
                oos.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            } finally {
                ois.close();
                oos.close();
            }
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, req.getMethod());
        }
    }

    public synchronized void addExpoter(Integer port, Exporter<?> exporter) {
        Map<String, Exporter<?>> exporterMap = exporters.get(port);
        if (exporterMap == null) {
            exporterMap = new HashMap<>();
            exporters.put(port, exporterMap);
        }
        exporterMap.put("/services/" + exporter.id(), exporter);
    }

    public synchronized Exporter<?> getExpoter(Integer port, String id) {
        Map<String, Exporter<?>> exporterMap = exporters.get(port);
        if (exporterMap != null) {
            return exporterMap.get(id);
        }
        return null;
    }

    public synchronized void removeExpoter(Exporter<?> exporter) {
        for (Map<String, Exporter<?>> exporterMap : exporters.values()) {
            exporterMap.remove("/services/" + exporter.id());
        }
    }

}
