# Freda RPC
[![framework](https://img.shields.io/badge/Framework-RPC-blue.svg?style=plastic)](#)
[![Languages](https://img.shields.io/badge/Language-Java-blue.svg)](#) 
[![Support](https://img.shields.io/badge/Support-jdk%201.7%2B-orange.svg)](#) 


## Introduction
A fast, lightweight, simple RPC framework base on Spring and Netty, without too much dependency, and the configuration is simple. If you like this, please click [star](https://github.com/thinkerwolf/FredaRpc/stargazers).

## Features
* Seamless integration with Spring. 
* Services can be configured via xml and annotations.
* High availability, load balance and failover.
* Support asynchronous and synchronous invoking.
* Support for freda and http network protocols


## Getting started
### Config services and reference by XML
#### Define service interface
``` java
package com.thinkerwolf.freda.example.bean;
public interface DemoService {
    String sayHello(String name);
}
```

#### Implement service interface
``` java
package com.thinkerwolf.freda.example.bean;
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String str) {
        System.out.println("Hello " + str);
        return "Hello " + str;
    }
}
```

#### Provider xml config
spring-freda-providor.xml
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:freda="http://www.freda.com/schema/freda" xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.freda.com/schema/freda
       http://www.freda.com/schema/freda/freda.xsd">
    <bean id="demoService" class="com.thinkerwolf.freda.example.bean.DemoServiceImpl"/>
    <!-- registry config zookeeper-->
    <freda:registry id="registry-zookeeper" host="127.0.0.1" port="2181" protocol="zookeeper"/>
    <!-- server config freda -->
    <freda:server id="server-1" host="127.0.0.1" port="8088" protocol="freda" timeout="1000" registries="registry-zookeeper"/>
    <!-- server http -->
    <freda:server id="server-2" host="127.0.0.1" port="8089" protocol="http" timeout="1000" registries="registry-zookeeper"/>
    <!-- export the service -->
    <freda:service interface="com.thinkerwolf.freda.example.bean.DemoService" ref="demoService" servers="server-1,server-2"/>
</beans>
```
#### Start provider 
```java
package com.thinkerwolf.freda.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class SpringProvidor {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-frade-providor.xml");
    }
}
```

#### Consumer xml config
spring-freda-consumer.xml
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:freda="http://www.freda.com/schema/freda" xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.freda.com/schema/freda
       http://www.freda.com/schema/freda/freda.xsd">
    <!-- registry config -->
    <freda:registry id="registry-zookeeper-1" host="127.0.0.1" port="2181" protocol="zookeeper"/>
    <!-- client config -->
    <freda:client id="client-1" protocol="freda"/>
    <!-- reference config -->
    <freda:reference id="demoService" clients="client-1" interface="com.thinkerwolf.freda.example.bean.DemoService" balance="hash" async="true"/>
</beans>
```
#### Start Consumer
``` java
package com.thinkerwolf.freda.example;

import com.thinkerwolf.freda.common.concurrent.Future;
import com.thinkerwolf.freda.example.bean.DemoService;
import com.thinkerwolf.freda.rpc.AsyncFutureListener;
import com.thinkerwolf.freda.rpc.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringComsumer {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-freda-consumer.xml");
        final DemoService ds = (DemoService) context.getBean("demoService");
        ds.sayHello("liyulong");
        Future<?> future = Context.getContext().getFuture();
        future.addListener(new AsyncFutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Throwable {
                if (future.isSuccess()) {
                    System.out.println(future.get());
                } else {
                    System.out.println(future.cause());
                }
            }
        });
    }
}
```
The SpringConsumer will print out `Hello liyunlong`.

### Config services and reference by annotation
#### Define service interface
We still use `DemoService` as the service interface.

#### Implement service interface with `Service` annotation
``` java
package com.thinkerwolf.freda.example.bean;
import com.thinkerwolf.freda.config.annotation.Service;
import org.springframework.stereotype.Component;
@Service(id = "demoService", interfaceClass = DemoService.class)
@Component("demoService")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String str) {
        System.out.println("Hello " + str);
        return "Hello " + str;
    }
}
```

#### Start annotation provider
The registry and server is configured in `SpringConfiguration`.
``` java
package com.thinkerwolf.freda.example;
import com.thinkerwolf.freda.config.spring.RegistryBean;
import com.thinkerwolf.freda.config.spring.ServerBean;
import com.thinkerwolf.freda.config.spring.annotation.FredaComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
```
For now, The provoder configuration finish. 

#### Start annotation consumer
First,use `Reference` annotation to inject the value to field
``` java
package com.thinkerwolf.freda.example.bean;
import com.thinkerwolf.freda.config.annotation.Reference;
import org.springframework.stereotype.Component;
@Component("demoServiceReference")
public class DemoServiceReference {
    @Reference(id = "demoService")
    private DemoService ds;
    public String hello() {
        return ds.sayHello("annotation freda");
    }
    @Reference(id = "demoService")
    public void setDs(DemoService ds) {
        this.ds = ds;
    }
}
```
Now, Let's start the consumer. The registry and server is configured in `SpringConfiguration`.

``` java
package com.thinkerwolf.freda.example;

import com.thinkerwolf.freda.config.spring.ClientBean;
import com.thinkerwolf.freda.config.spring.RegistryBean;
import com.thinkerwolf.freda.example.bean.DemoServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
```
The SpringAnnotationConsumer will print out `Hello annotation freda`.

## Contact
- Mail: wukai213@gmail.com
