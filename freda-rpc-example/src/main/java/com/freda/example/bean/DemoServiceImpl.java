package com.freda.example.bean;

import org.springframework.stereotype.Component;

import com.freda.config.annotation.Service;

@Service(id = "demoService", interfaceClass = DemoService.class)
@Component("demoService")
public class DemoServiceImpl implements DemoService {

    private Double persent;

    private boolean server;

    private int num;

    private String hello;

    public Double getPersent() {
        return persent;
    }

    public void setPersent(Double persent) {
        this.persent = persent;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String str) {
        System.out.println("Hello " + str);
        return "Hello " + str;
    }
}
