package com.nettyrpc.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticsUtils {
	
	AtomicInteger ai = new AtomicInteger();
	
	private static final Logger logger = LoggerFactory.getLogger(StaticsUtils.class);
	
	
	public void addNum() {
		int num = ai.incrementAndGet();
		//System.out.println("Statics num#" + num);
		if (logger.isDebugEnabled()) {
			logger.debug("static num # " + num);
		}
	}
	
	
	
}
