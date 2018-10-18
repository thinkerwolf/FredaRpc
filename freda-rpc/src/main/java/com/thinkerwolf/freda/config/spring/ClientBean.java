package com.thinkerwolf.freda.config.spring;

import org.springframework.beans.factory.InitializingBean;

import com.thinkerwolf.freda.config.ClientConfig;

public class ClientBean extends ClientConfig implements InitializingBean {

    private static final long serialVersionUID = -9141188348378091958L;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
