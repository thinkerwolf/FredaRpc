package com.freda.remoting.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractWebServerFactory implements WebServerFactory {
    /**
     * <contextPath, <servletName, Servlet>>
     */
    protected Map<String, Map<String, ServletConfig>> servletMap = new HashMap<>();
    /**
     * all contextPaths
     */
    protected Set<String> contextPaths = new HashSet<String>();
    protected String baseDir;
    /**
     * port
     */
    private int port;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    public synchronized void addServlet(String contextPath, ServletConfig servlet) {
        Map<String, ServletConfig> map = servletMap.get(contextPath);
        if (map == null) {
            map = new HashMap<>();
            servletMap.put(contextPath, map);
        }
        if (!contextPaths.contains(contextPath)) {
            contextPaths.add(contextPath);
        }
        map.put(servlet.getName(), servlet);
    }

    public synchronized void addContextPath(String contextPath) {
        contextPaths.add(contextPath);
    }

    @Override
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }


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
}
