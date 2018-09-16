package com.freda.example.bean;

import com.freda.config.annotation.Service;
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
