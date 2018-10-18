package com.thinkerwolf.freda.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkerwolf.freda.config.Application;
import com.thinkerwolf.freda.config.ReferenceConfig;

@Deprecated
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private Application conf;
    private String confFilePath;

    public Bootstrap() {

    }

    public void start() {
        try {
            if (confFilePath == null) {
                this.conf = Application.newConfiguration();
            } else {
                this.conf = Application.newConfiguration(confFilePath);
            }
        } catch (Exception e) {
            logger.error("FredaProvidor init error", e);
        }
    }

    public Application getConf() {
        return conf;
    }

    public void setConfFilePath(String confFilePath) {
        this.confFilePath = confFilePath;
    }

    public <T> T refer(Class<T> clazz) {
        ReferenceConfig<T> rc = this.conf.getReferenceConf(clazz);
        return rc.getRef();
    }

}
