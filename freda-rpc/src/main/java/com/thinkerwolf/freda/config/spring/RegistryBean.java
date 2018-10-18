package com.thinkerwolf.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.thinkerwolf.freda.config.Application;
import com.thinkerwolf.freda.config.RegistryConfig;

public class RegistryBean extends RegistryConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Application.getInstance().addRegistryConfig(this);
    }

}
