package com.freda.config.spring.xml;

import com.freda.config.spring.ClientBean;
import com.freda.config.spring.ReferenceBean;
import com.freda.config.spring.RegistryBean;
import com.freda.config.spring.ServerBean;
import com.freda.config.spring.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 集成到spring中
 *
 * @author wukai
 */
public class FredaNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("service", new FredaBeanDefinitionParser(ServiceBean.class));
        registerBeanDefinitionParser("reference", new FredaBeanDefinitionParser(ReferenceBean.class));
        registerBeanDefinitionParser("registry", new FredaBeanDefinitionParser(RegistryBean.class));
        registerBeanDefinitionParser("server", new FredaBeanDefinitionParser(ServerBean.class));
        registerBeanDefinitionParser("client", new FredaBeanDefinitionParser(ClientBean.class));
    }

}
