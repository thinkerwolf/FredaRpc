package com.freda.test;

import com.freda.bootstrap.FredaConsumer;
import com.freda.example.DemoService;

public class ConsumerTest {

    public static void main(String args[]) {
        FredaConsumer consumer = new FredaConsumer();
        consumer.setConfFilePath("classpath:freda-consumer.xml");
        consumer.start();

        DemoService ds = consumer.refer(DemoService.class);
        ds.sayHello("freda");
    }
}
