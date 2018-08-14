package com.nettyrpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class StaticsUtils {

    private static final Logger logger = LoggerFactory.getLogger(StaticsUtils.class);
    AtomicInteger ai = new AtomicInteger();

    public void addNum() {
        int num = ai.incrementAndGet();
        //System.out.println("Statics num#" + num);
        if (logger.isDebugEnabled()) {
            logger.debug("static num # " + num);
        }
    }


}
