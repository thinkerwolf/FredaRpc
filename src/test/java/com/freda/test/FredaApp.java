package com.freda.test;

import com.freda.common.conf.NetConfig;
import com.freda.config.Configuration;
import com.freda.config.ServiceConfig;

import org.apache.tomcat.util.descriptor.web.MessageDestination;
import org.junit.Test;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FredaApp {

	public void configurationTest() {
		try {
			URL url = Configuration.class.getClassLoader().getResource("freda.xml");
			Configuration.newConfiguration(url.openConnection().getInputStream());
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

		NetConfig nc = new NetConfig();
		nc.setIp("127.0.0.1");
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
