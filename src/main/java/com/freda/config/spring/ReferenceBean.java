package com.freda.config.spring;

import com.freda.config.ClientConfig;
import com.freda.config.Configuration;
import com.freda.config.ReferenceConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * spring reference bean
 *
 * @author wukai
 */
public class ReferenceBean<T> extends ReferenceConfig<T>
		implements FactoryBean<T>, ApplicationContextAware, InitializingBean, DisposableBean {
	private ApplicationContext context;
	
	private static final String CONSUMER_SPLIT = ",";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.setConf(Configuration.getInstance());
		if (!StringUtils.hasText(getId())) {
			this.setId(getInterface());
		}
		Map<String, ClientBean> beanMap = context.getBeansOfType(ClientBean.class);
		if (beanMap == null || beanMap.size() == 0) {
			return;
		}
		List<ClientConfig> clientConfs = new LinkedList<>();
		if (StringUtils.hasText(clients)) {
			for (String cId : clients.split(CONSUMER_SPLIT)) {
				ClientConfig cc = getClientConfig(beanMap, cId);
				if (cc != null) {
					clientConfs.add(cc);
				}
			}
		} else {
			for (ClientBean cc : beanMap.values()) {
				clientConfs.add(cc);
			}
		}
		this.setClientConfs(clientConfs);
		
		if (clientConfs.size() > 0) {
			Map<String, RegistryBean> registryBeanMap = context.getBeansOfType(RegistryBean.class);
			for (RegistryBean rb : registryBeanMap.values()) {
				this.addRegistryConf(rb);
			}
			export();
			Configuration.getInstance().addReferenceConf(this);
		}
	}
	
	private ClientConfig getClientConfig(Map<String, ClientBean> beanMap, String id) {
		for (ClientBean b : beanMap.values()) {
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

	@Override
	public void destroy() throws Exception {

	}

	@Override
	public T getObject() throws Exception {
		return getRef();
	}

	@Override
	public Class<?> getObjectType() {
		return getInterfaceClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
