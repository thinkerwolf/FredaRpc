package com.thinkerwolf.freda.rpc.http;

import org.apache.commons.lang3.StringUtils;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.common.ServiceLoader;
import com.thinkerwolf.freda.rpc.Exporter;
import com.thinkerwolf.freda.rpc.RequestMessage;
import com.thinkerwolf.freda.rpc.ResponseMessage;
import com.thinkerwolf.freda.serialization.ObjectInput;
import com.thinkerwolf.freda.serialization.ObjectOutput;
import com.thinkerwolf.freda.serialization.Serializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
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
            String serializtion = req.getParameter("serialization");
            if (StringUtils.isEmpty(serializtion)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Serializtion not found");
                return;
            }
            try {
                Serializer serializer = ServiceLoader.getService(serializtion, Serializer.class);

                ObjectInput oi = serializer.deserialize(req.getInputStream());
                RequestMessage requestMessage = oi.readObject(RequestMessage.class);
                oi.close();
                Object result = exporter.invoke(getRemoteNet(req), requestMessage.getMethodName(), requestMessage.getParameterTypes(), requestMessage.getArgs());
                
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setSuccess(true);
                responseMessage.setResult(result);
                responseMessage.setId(requestMessage.getRequestId());
                
                ObjectOutput oo = serializer.serialize(resp.getOutputStream());
                resp.setContentType("text/json");
                oo.writeObject(responseMessage);
                oo.close();
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, req.getMethod());
        }
    }
    
    private static Net getRemoteNet(HttpServletRequest req) {
    	String serializtion = req.getParameter("serialization");
    	Net net = new Net(req.getRemoteHost(), req.getRemotePort(), "http", 0, serializtion);
    	return net;
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
