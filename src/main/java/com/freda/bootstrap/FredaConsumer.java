package com.freda.bootstrap;

import com.freda.common.conf.Configuration;
import com.freda.remoting.RemotingClient;
import com.freda.remoting.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FredaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FredaProvidor.class);
    private Configuration conf;
    private RemotingClient remoting;
    private String confFilePath;

    public FredaConsumer() {

    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }

    private void init() {
        try {
            if (confFilePath != null) {
                this.conf = Configuration.newConfiguration();
            } else {
                this.conf = Configuration.newConfiguration(confFilePath);
            }
            this.remoting = new NettyClient(conf);
        } catch (Exception e) {
            logger.error("FredaProvidor init error", e);
        }
    }


    public void start() {
        init();
        this.remoting.start();
    }

    public <T> T refer(Class<T> clazz) {
        return this.remoting.invokeSync(clazz);
    }


}
