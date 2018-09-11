package com.freda.test.example;

import org.springframework.stereotype.Component;

import com.freda.config.annotation.Reference;

@Component("demoServiceReference")
public class DemoServiceReference {
	
	@Reference(id = "demoService", interfaceClass = DemoService.class, balance = "hash", cluster = "fastover", registry = "registry-1", retries = 2)
	private DemoService ds;

	public String hello() {
		return ds.sayHello("annotation freda");
	}

}
