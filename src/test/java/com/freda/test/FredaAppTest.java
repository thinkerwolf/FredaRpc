package com.freda.test;

import java.net.URL;

import org.junit.Test;

import com.freda.config.Configuration;
import com.freda.config.ServiceConfig;

public class FredaAppTest {

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
		
		
	}
	
}
