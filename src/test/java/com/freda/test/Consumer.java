package com.freda.test;

import com.freda.bootstrap.Bootstrap;
import com.freda.example.DemoService;

public class Consumer {

	public static void main(String args[]) {
		try {
			Bootstrap b = new Bootstrap();
			b.setConfFilePath("classpath:freda-consumer.xml");
			b.start();
			DemoService ds = b.refer(DemoService.class);
			ds.sayHello("freda");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
