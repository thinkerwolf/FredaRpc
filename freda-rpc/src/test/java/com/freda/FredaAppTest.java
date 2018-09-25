package com.freda;

import com.freda.common.Net;
import com.freda.config.Application;
import com.freda.config.ServiceConfig;
import org.junit.Test;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FredaAppTest {

    public void configurationTest() {
        try {
            URL url = Application.class.getClassLoader().getResource("freda.xml");
            Application.newConfiguration(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void javaTest() {
        Class<?> clazz = ServiceConfig.class;
        try {
            System.out.println(clazz.getSuperclass().getDeclaredField("id"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        System.out.println("0<<1 #" + (2 << 1));
        Net nc = new Net();
        nc.setHost("127.0.0.1");
        nc.setPort(9000);
        System.out.println(nc.uri());
    }

    @Test
    public void pathTest() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        System.out.println(url.getPath());
    }

    @Test
    public void messageDigest() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] b = md.digest("s".getBytes());
            System.out.println(Arrays.toString(b));

            System.out.println(0xFFFFFFFFL & 8);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
    
    

}
