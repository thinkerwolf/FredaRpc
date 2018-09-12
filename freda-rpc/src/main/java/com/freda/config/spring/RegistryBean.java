package com.freda.config.spring;

import com.freda.config.Configuration;
import com.freda.config.RegistryConfig;

import org.springframework.beans.factory.InitializingBean;

public class RegistryBean extends RegistryConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Configuration.getInstance().addRegistryConfig(this);
    }

}
