package com.freda.test;

import java.net.URL;

import org.junit.Test;

import com.freda.config.Configuration;

public class FredaAppTest {

	@Test
	public void configurationTest() {
		try {
			URL url = Configuration.class.getClassLoader().getResource("freda.xml");
			Configuration.newConfiguration(url.openConnection().getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
