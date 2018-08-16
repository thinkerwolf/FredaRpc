package com.freda.test;

import com.freda.common.conf.Configuration;
import com.freda.example.DemoService;
import com.freda.remoting.netty.NettyClient;

public class NettyClientTest {
	public static void main(String[] args) throws Exception {
		Configuration conf = Configuration.newConfiguration("freda-consumer.xml");
		NettyClient client = new NettyClient(conf);
		client.start();
		DemoService ds = client.invokeSync(DemoService.class);
		System.out.println(ds.sayHello("freda"));
	}
}
