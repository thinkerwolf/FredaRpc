package com.nettyrpc.example;

public class DemoServiceImpl implements DemoService {
	@Override
	public String sayHello(String str) {
		return "Hello " + str;
	}
}
