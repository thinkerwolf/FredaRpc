package com.freda.bootstrap;

import com.freda.config.Configuration;
import com.freda.remoting.RemotingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private Configuration conf;
    private String confFilePath;

    public Bootstrap() {

    }

    public void start() {
        try {
            if (confFilePath == null) {
                this.conf = Configuration.newConfiguration();
            } else {
                this.conf = Configuration.newConfiguration(confFilePath);
            }
        } catch (Exception e) {
            logger.error("FredaProvidor init error", e);
        }
    }

    public Configuration getConf() {
        return conf;
    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }

    public <T> T refer(Class<T> clazz) {
        RemotingClient rc = this.conf.getRefRemoting(clazz);
        return rc.sendSync(clazz);
    }

}
