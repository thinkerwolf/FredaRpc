package com.freda.example;

import com.freda.config.spring.RegistryBean;
import com.freda.config.spring.ServerBean;
import com.freda.config.spring.annotation.FredaComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * annotation provider test
 *
 * @author wukai
 */
public class SpringAnnotationProvidor {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    }

    @Configuration
    @FredaComponentScan
    public static class SpringConfiguration {


        @Bean("provider-1")
        ServerBean provider_1() {
            ServerBean netBean = new ServerBean();
            netBean.setProtocol("freda");
            netBean.setHost("127.0.0.1");
            netBean.setPort(8088);
            return netBean;
        }

        @Bean("registry-1")
        RegistryBean registry_1() {
            RegistryBean rb = new RegistryBean();
            rb.setHost("127.0.0.1");
            rb.setPort(2181);
            rb.setProtocol("zookeeper");
            return rb;
        }

    }

}
