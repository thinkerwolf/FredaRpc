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

    private String confFilePath;

    public FredaProvidor() {

    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }
    
    public void start() {
        try {
            this.conf = Configuration.newConfiguration(confFilePath);
        } catch (Exception e) {
            logger.error("FredaProvidor init error", e);
        }
    }
    

}
