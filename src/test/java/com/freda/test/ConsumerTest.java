package com.freda.test;

import com.freda.bootstrap.FredaConsumer;
import com.freda.example.DemoService;

public class ConsumerTest {

    public static void main(String args[]) {
    	try {
			FredaConsumer consumer = new FredaConsumer();
			consumer.setConfFilePath("classpath:freda-consumer.xml");
			consumer.start();
			
			
			DemoService ds = consumer.refer(DemoService.class);
			ds.sayHello("freda");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
       
    }
}
