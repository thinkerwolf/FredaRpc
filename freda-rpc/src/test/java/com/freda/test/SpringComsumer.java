package com.freda.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.test.example.DemoService;

public class SpringComsumer {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade-consumer.xml");
		final DemoService ds = (DemoService) context.getBean("demoService");

		ExecutorService es = Executors.newFixedThreadPool(100);
		int i = 100000;
		// long startTime = System.currentTimeMillis();
		final int t = i;
		while (i-- > 0) {
			final int index = i;
			es.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(ds.sayHello("liyulong-" + (t - index)));
				}
			});
		}
	}
}
