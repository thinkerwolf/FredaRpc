package com.freda.config.spring;

import com.freda.config.Configuration;
import com.freda.config.ServerConfig;
import com.freda.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Spring service bean
 *
 * @author wukai
 */
public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setConf(Configuration.getInstance());
		Map<String, ServerBean> beanMap = context.getBeansOfType(ServerBean.class);
		if (beanMap == null || beanMap.size() == 0) {
			return;
		}
		List<ServerConfig> serverConfigs = new LinkedList<>();
		if (StringUtils.hasText(servers)) {
			for (String sId : servers.split(",")) {
				ServerConfig sc = getServerConfig(beanMap, sId);
				if (sc != null) {
					serverConfigs.add(sc);
				}
			}
		} else {
			for (ServerBean sc : beanMap.values()) {
				serverConfigs.add(sc);
			}
		}
		this.setServerConfigs(serverConfigs);
		if (serverConfigs.size() > 0) {
//			Map<String, RegistryBean> registryBeanMap = context.getBeansOfType(RegistryBean.class);
//			for (RegistryBean rb : registryBeanMap.values()) {
//				this.addRegistryConf(rb);
//			}
			export();
			Configuration.getInstance().addServiceConf(this);
		}
	}
	
	private ServerConfig getServerConfig(Map<String, ServerBean> beanMap, String id) {
		for (ServerBean b : beanMap.values()) {
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