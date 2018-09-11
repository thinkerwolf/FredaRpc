package com.freda.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.freda.config.spring.NetBean;
import com.freda.config.spring.RegistryBean;
import com.freda.config.spring.annotation.FredaComponentScan;

/**
 * annotation provider test
 * 
 * @author wukai
 *
 */
public class SpringAnnotationProvidor {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
	}
	
	@Configuration
	@ComponentScan
	@FredaComponentScan
	public static class SpringConfiguration {

		@Bean("comsumer-1")
		NetBean consumer_1() {
			NetBean netBean = new NetBean();
			netBean.setProtocol("freda");
			netBean.setServer(false);
			return netBean;
		}

		@Bean("provider-1")
		NetBean provider_1() {
			NetBean netBean = new NetBean();
			netBean.setProtocol("freda");
			netBean.setIp("127.0.0.1");
			netBean.setPort(8088);
			netBean.setBossThreads(2);
			netBean.setWorkerThreads(4);
			netBean.setServer(true);
			return netBean;
		}

		@Bean("registry-1")
		RegistryBean registry_1() {
			RegistryBean rb = new RegistryBean();
			rb.setIp("127.0.0.1");
			rb.setPort(2181);
			rb.setProtocol("zookeeper");
			return rb;
		}

	}

}
