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
        @Bean
        ServerBean provider_1() {
            ServerBean bean = new ServerBean();
            bean.setId("provider-1");
            bean.setProtocol("freda");
            bean.setHost("127.0.0.1");
            bean.setPort(8088);
            return bean;
        }
        @Bean("registry-1")
        RegistryBean registry_1() {
            RegistryBean bean = new RegistryBean();
            bean.setId("registry-1");
            bean.setHost("127.0.0.1");
            bean.setPort(2181);
            bean.setProtocol("zookeeper");
            return bean;
        }
    }
}
