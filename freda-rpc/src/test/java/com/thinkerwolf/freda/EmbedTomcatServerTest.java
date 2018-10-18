package com.thinkerwolf.freda;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;
import java.io.File;
import java.io.IOException;

/**
 * 内嵌Tomcat测试
 *
 * @author wukai
 */
public class EmbedTomcatServerTest {

    static final int port = 8080;
    static final String docBase = "c:/tmp/tomcat";

    public static void main(String[] args) throws Exception {

        File file = new File(docBase);
        if (file.isDirectory()) {
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

        tomcat.addServlet(contextPath, "InternalServlet", new InnerServlet());
        context.addServletMappingDecoded("/home/*", "InternalServlet");
        tomcat.start();
        tomcat.getServer().await();
    }

    /**
     * Return the absolute temp dir for given web server.
     *
     * @param prefix server name
     * @return The temp dir for given server.
     */
    protected static final File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + 8080);
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException ex) {
            throw new WebServiceException(
                    "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
        }
    }

    static class InnerServlet extends HttpServlet {
        /**
         *
         */
        private static final long serialVersionUID = -8826499817240741155L;

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.service(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            String requestUri = req.getRequestURI();
            System.out.println(requestUri);
            System.out.println(req.getProtocol());
            System.out.println(req.getQueryString());
            System.out.println(req.getRemoteUser());
            //super.doGet(req, resp);
        }


        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doPut(req, resp);
        }
    }

}
