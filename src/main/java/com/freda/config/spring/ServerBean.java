package com.freda.config.spring;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.freda.config.RegistryConfig;
import com.freda.config.ServerConfig;

public class ServerBean extends ServerConfig implements InitializingBean, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, RegistryBean> registryBeanMap = context.getBeansOfType(RegistryBean.class);
		List<RegistryConfig> registryConfigs = new LinkedList<>();
		if (StringUtils.hasText(registries)) {
			for (String id : registries.split(",")) {
				RegistryConfig registryConfig = getRegistryConfig(registryBeanMap, id);
				if (registryConfig == null) {
					throw new RuntimeException("can't find registry named [" + id + "]");
				}
				registryConfigs.add(registryConfig);
			}
		} else {
			registryConfigs.addAll(registryBeanMap.values());
		}
		this.setRegistryConfigs(registryConfigs);
	}

	private RegistryConfig getRegistryConfig(Map<String, RegistryBean> registryBeanMap, String id) {
		for (RegistryBean b : registryBeanMap.values()) {
			if (id.equals(b.getId())) {
				return b;
			}
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
