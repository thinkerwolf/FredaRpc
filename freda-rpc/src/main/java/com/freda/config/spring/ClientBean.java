package com.freda.config.spring;

import com.freda.config.ClientConfig;
import org.springframework.beans.factory.InitializingBean;

public class ClientBean extends ClientConfig implements InitializingBean {

    private static final long serialVersionUID = -9141188348378091958L;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
