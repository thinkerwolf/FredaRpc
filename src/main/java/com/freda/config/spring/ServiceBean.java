package com.freda.config.spring;

import com.freda.config.Configuration;
import com.freda.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Spring service bean
 *
 * @author wukai
 */
public class ServiceBean extends ServiceConfig implements InitializingBean, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, NetBean> nettyBeanMap = context.getBeansOfType(NetBean.class);
        if (nettyBeanMap == null || nettyBeanMap.size() == 0) {
            return;
        }
        this.setConf(Configuration.getInstance());
        int num = 0;
        for (NetBean nb : nettyBeanMap.values()) {
            if (nb.isServer() && nb.isUseable()) {
                num++;
                this.addNettyConf(nb);
            }
        }
        if (num > 0) {
            Map<String, RegistryBean> registryBeanMap = context.getBeansOfType(RegistryBean.class);
            this.addRegistryConfs(registryBeanMap.values());
            Configuration.getInstance().addServiceConfig(this);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
