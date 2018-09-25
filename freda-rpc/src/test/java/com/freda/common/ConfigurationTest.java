package com.freda.common;

import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void conf() {
		int n = Configuration.getIntProperty(Configuration.NETTY_BOSS_THREADS, Constants.DEFAULT_THREAD_NUM / 2);
		System.out.println(n);
	}

}
