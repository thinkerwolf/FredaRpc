package com.freda.bootstrap;

import com.freda.common.conf.Configuration;
import com.freda.remoting.RemotingServer;
import com.freda.remoting.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Freda
 */
public class FredaProvidor {

    private static final Logger logger = LoggerFactory.getLogger(FredaProvidor.class);


    private Configuration conf;

    private RemotingServer remoting;

    private String confFilePath;

    public FredaProvidor() {

    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }

    private void init() {
        try {
            this.conf = Configuration.newConfiguration();
            this.remoting = new NettyServer(conf);
        } catch (Exception e) {
            logger.error("FredaProvidor init error", e);
        }
    }


    public void start() {
        init();
        this.remoting.start();
    }


}
