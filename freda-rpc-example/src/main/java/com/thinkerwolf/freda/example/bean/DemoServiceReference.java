package com.thinkerwolf.freda.example.bean;

import org.springframework.stereotype.Component;

import com.thinkerwolf.freda.config.annotation.Reference;

@Component("demoServiceReference")
public class DemoServiceReference {
    @Reference(id = "demoService", interfaceClass = DemoService.class, clients = "client_1", balance = "hash", cluster = "failfast", registries = "registry-1", retries = 2)
    private DemoService ds;
    public String hello() {
        return ds.sayHello("annotation freda");
    }
    @Reference(id = "demoService")
    public void setDs(DemoService ds) {
        this.ds = ds;
        System.out.println("ds 注入完成");
    }
}
