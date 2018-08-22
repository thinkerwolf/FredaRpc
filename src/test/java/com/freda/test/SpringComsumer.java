package com.freda.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.example.DemoService;

public class SpringComsumer {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade-consumer.xml");
		DemoService ds = (DemoService) context.getBean("demoService");
		System.out.println(ds.sayHello("liyulong"));
	}
}
