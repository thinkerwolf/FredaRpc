package com.thinkerwolf.freda.example.bean;

import org.springframework.stereotype.Component;

import com.thinkerwolf.freda.config.annotation.Service;

@Service(id = "demoService", interfaceClass = DemoService.class)
@Component("demoService")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String str) {
        System.out.println("Hello " + str);
        return "Hello " + str;
    }
}
