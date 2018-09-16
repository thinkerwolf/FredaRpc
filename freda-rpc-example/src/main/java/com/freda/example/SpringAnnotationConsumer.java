package com.freda.example;

import com.freda.config.spring.ClientBean;
import com.freda.config.spring.RegistryBean;
import com.freda.example.bean.DemoServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * annotation consumer test
 *
 * @author wukai
 */
public class SpringAnnotationConsumer {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        DemoServiceReference dsr = (DemoServiceReference) context.getBean("demoServiceReference");
        System.out.println(dsr.hello());
    }
    @Configuration
    @ComponentScan
    public static class SpringConfiguration {
        @Bean
        ClientBean client1() {
            ClientBean netBean = new ClientBean();
            netBean.setId("client_1");
            netBean.setProtocol("freda");
            return netBean;
        }
        @Bean
        RegistryBean registry1() {
            RegistryBean rb = new RegistryBean();
            rb.setId("registry-1");
            rb.setHost("127.0.0.1");
            rb.setPort(2181);
            rb.setProtocol("zookeeper");
            return rb;
        }
    }
}
