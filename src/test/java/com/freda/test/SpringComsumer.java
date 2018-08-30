package com.freda.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.test.example.DemoService;

public class SpringComsumer {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade-consumer.xml");
        DemoService ds = (DemoService) context.getBean("demoService");
        
        int i = 1000;
        long startTime = System.currentTimeMillis();
        while (i-- > 0) {
        	 System.out.println(ds.sayHello("liyulong-" + i));
        }
        System.out.println("total time -> " + (System.currentTimeMillis() - startTime));
       
    }
}
