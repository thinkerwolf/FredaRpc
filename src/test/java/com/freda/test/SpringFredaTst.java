package com.freda.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.config.ServiceConfig;
import com.freda.config.spring.ServiceBean;

public class SpringFredaTst {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade.xml");
		Object obj = context.getBean("demoService");
		ServiceBean sb = (ServiceBean) context.getBean("demoService_service");
		System.out.println(sb.getInterface());
		System.out.println(sb.getRef());
	}
}
