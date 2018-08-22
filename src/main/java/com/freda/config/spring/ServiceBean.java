package com.freda.config.spring;

import com.freda.config.Configuration;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.freda.config.ServiceConfig;

/**
 * Spring service bean
 * 
 * @author wukai
 *
 */
public class ServiceBean extends ServiceConfig implements InitializingBean, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, NettyBean> nettyBeanMap  = context.getBeansOfType(NettyBean.class);
		
		if (nettyBeanMap == null || nettyBeanMap.size() == 0) {
			return;
		}
		int num = 0;
		for (NettyBean nb : nettyBeanMap.values()) {
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
