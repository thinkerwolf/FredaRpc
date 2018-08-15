package com.freda.remote;

import java.util.concurrent.ThreadFactory;

public class RpcThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable arg0) {
        Thread thread = new Thread(arg0);
        thread.setName("rpc-thread-");
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }

}
