package com.freda.config.spring;

import com.freda.config.ClientConfig;
import com.freda.config.Configuration;
import com.freda.config.ReferenceConfig;
import com.freda.config.RegistryConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

import static com.freda.config.ConfigUtil.findClientConfig;
import static com.freda.config.ConfigUtil.findRegistryConfig;

/**
 * spring reference bean
 *
 * @author wukai
 */
public class ReferenceBean<T> extends ReferenceConfig<T>
        implements FactoryBean<T>, ApplicationContextAware, InitializingBean, DisposableBean {

    private static final long serialVersionUID = -6731451098024965342L;
    private static final String CONSUMER_SPLIT = ",";
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setConf(Configuration.getInstance());
        if (!StringUtils.hasText(getId())) {
            this.setId(getInterface());
        }

        List<ClientConfig> clientBeans = new LinkedList<ClientConfig>(context.getBeansOfType(ClientBean.class).values());
        if (clientBeans.size() == 0) {
            return;
        }
        List<ClientConfig> clientConfs = new LinkedList<>();
        if (StringUtils.hasText(clients)) {
            for (String cId : clients.split(CONSUMER_SPLIT)) {
                ClientConfig cc = findClientConfig(clientBeans, cId);
                if (cc != null) {
                    clientConfs.add(cc);
                }
            }
        } else {
            for (ClientConfig cc : clientBeans) {
                clientConfs.add(cc);
            }
        }
        this.setClientConfigs(clientConfs);

        if (clientConfs.size() > 0) {
            List<RegistryConfig> registryBeans = new LinkedList<RegistryConfig>(context.getBeansOfType(RegistryBean.class).values());
            if (StringUtils.hasText(registries)) {
                for (String id : registries.split(CONSUMER_SPLIT)) {
                    RegistryConfig registryConfig = findRegistryConfig(registryBeans, id);
                    if (registryConfig == null) {
                        throw new RuntimeException("can't find registry named [" + id + "]");
                    }
                    this.addRegistryConf(registryConfig);
                }
            } else {
                for (RegistryConfig rb : registryBeans) {
                    this.addRegistryConf(rb);
                }
            }
            export();
            Configuration.getInstance().addReferenceConf(this);
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
