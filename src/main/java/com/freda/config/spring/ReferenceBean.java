package com.freda.config.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.freda.config.Configuration;
import com.freda.config.ReferenceConfig;
import com.freda.remoting.RemotingClient;

/**
 * spring reference bean
 * 
 * @author wukai
 *
 */
@SuppressWarnings("rawtypes")
public class ReferenceBean extends ReferenceConfig
		implements FactoryBean, ApplicationContextAware, InitializingBean, DisposableBean {

	private ApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, NettyBean> nettyBeanMap = context.getBeansOfType(NettyBean.class);
		if (nettyBeanMap == null || nettyBeanMap.size() == 0) {
			return;
		}
		if (!StringUtils.hasText(getId())) {
			this.setId(getInterface());
		}
		int num = 0;
		for (NettyBean nb : nettyBeanMap.values()) {
			if (!nb.isServer()) {
				this.setNettyConf(nb);
				num++;
				break;
			}
		}
		if (num > 0) {
			Map<String, RegistryBean> registryBeanMap = context.getBeansOfType(RegistryBean.class);
			this.addRegistryConfs(registryBeanMap.values());
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void destroy() throws Exception {

	}

	@Override
	public Object getObject() throws Exception {
		if (getRef() == null) {
			RemotingClient client = Configuration.getInstance().addReferenceConfig(this);
			Object obj = client.invokeSync(this.getInterfaceClass());
			this.setRef(obj);
		}
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
