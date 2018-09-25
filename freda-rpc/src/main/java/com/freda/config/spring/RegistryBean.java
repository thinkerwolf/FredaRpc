package com.freda.config.spring;

import com.freda.config.Application;
import com.freda.config.RegistryConfig;
import org.springframework.beans.factory.InitializingBean;

public class RegistryBean extends RegistryConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Application.getInstance().addRegistryConfig(this);
    }

}
