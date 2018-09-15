package com.freda.remoting.web.tomcat;

import com.freda.remoting.web.AbstractWebServerFactory;
import com.freda.remoting.web.ServletConfig;
import com.freda.remoting.web.WebServer;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.util.Map;

public class TomcatWebServerFactory extends AbstractWebServerFactory {

    @SuppressWarnings("deprecation")
    @Override
    public WebServer getWebServer() {
        File file = baseDir == null ? createTempDir("tomcat") : new File(baseDir);
        if (file.isDirectory() && file.exists()) {
            file.mkdirs();
        }
        baseDir = file.getAbsolutePath();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(getPort());
        tomcat.setBaseDir(baseDir);
        tomcat.getConnector().setProperty("URIEncoding", "UTF-8");
        tomcat.getConnector().setProperty("connectionTimeout", "60000");
        tomcat.getConnector().setProperty("maxKeepAliveRequests", "-1");
        tomcat.getConnector().setProtocol("org.apache.coyote.http11.Http11NioProtocol");
        initTest(tomcat);
        return getTomcatWebServer(tomcat);
    }

    private void initTest(Tomcat tomcat) {
        for (String contextPath : contextPaths) {
            Context context = tomcat.addContext(contextPath, baseDir);
            Map<String, ServletConfig> map = servletMap.get(contextPath);
            if (map != null) {
                for (ServletConfig sc : map.values()) {
                    Tomcat.addServlet(context, sc.getName(), sc.getServlet());
                    context.addServletMappingDecoded(sc.getMappingPath(), sc.getName());
                }
            }
        }
    }

    private TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
        return new TomcatWebServer(tomcat);
    }

}
