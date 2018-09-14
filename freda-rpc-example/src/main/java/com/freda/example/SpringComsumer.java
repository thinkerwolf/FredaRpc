package com.freda.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.freda.example.bean.DemoService;
import com.freda.rpc.AsyncCallback;

public class SpringComsumer {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade-consumer.xml");
		final DemoService ds = (DemoService) context.getBean("demoService");

		ExecutorService es = Executors.newFixedThreadPool(2, new ThreadFactory() {
			private AtomicInteger id = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("comsumer-pool-" + id.getAndIncrement());
				return t;
			}
		});
		int i = 2;
		final long startTime = System.currentTimeMillis();
		while (i-- > 0) {
			final int index = i;
			es.execute(new Runnable() {
				@Override
				public void run() {
					Thread th = Thread.currentThread();
					Object r = ds.sayHello("[" + th.getName() + "] liyulong-" + index);
					System.out.println(r);
				}
			});
		}

		es.shutdown();
		try {
			es.awaitTermination(1000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!es.isTerminated()) {

		}
		System.out.println("spend time " + (System.currentTimeMillis() - startTime));
	}
}
