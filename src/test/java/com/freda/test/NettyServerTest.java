package com.freda.test;

import com.freda.common.conf.Configuration;
import com.freda.remoting.netty.NettyServer;

public class NettyServerTest {
	public static void main(String[] args) throws Exception {
		Configuration conf = Configuration.newConfiguration("com/freda/test/freda-providor.xml");
		NettyServer server = new NettyServer(conf);
		server.start();
	}
}
