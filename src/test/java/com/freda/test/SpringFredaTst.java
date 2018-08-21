package com.freda.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.config.ServiceConfig;

public class SpringFredaTst {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade.xml");
		context.getBean(ServiceConfig.class);
		context.getBean("demoService");
		
	}
}
