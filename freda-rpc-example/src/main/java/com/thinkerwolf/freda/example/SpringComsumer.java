package com.thinkerwolf.freda.example;

import com.thinkerwolf.freda.common.concurrent.Future;
import com.thinkerwolf.freda.example.bean.DemoService;
import com.thinkerwolf.freda.rpc.AsyncFutureListener;
import com.thinkerwolf.freda.rpc.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpringComsumer {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-freda-consumer.xml");
        final DemoService ds = (DemoService) context.getBean("demoService");
        ExecutorService es = Executors.newFixedThreadPool(3, new ThreadFactory() {
            private AtomicInteger id = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("comsumer-pool-" + id.getAndIncrement());
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        });
        int i = 50;
        final long startTime = System.currentTimeMillis();
        while (i-- > 0) {
            final int index = i;
            es.execute(new Runnable() {
                @Override
                public void run() {
                    Thread th = Thread.currentThread();
                    Object r = ds.sayHello("[" + th.getName() + "] liyulong-" + index);
                    Future<?> future = Context.getContext().getFuture();
                    future.addListener(new AsyncFutureListener<Object>() {
                        @Override
                        public void operationComplete(Future<Object> future) throws Throwable {
                            if (future.isSuccess()) {
                                System.out.println(future.get());
                            } else {
                                System.out.println(future.cause());
                            }
                        }
                    });
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
