package com.freda.example.bean;

import com.freda.rpc.AsyncCallback;

public interface DemoService {

    public String sayHello(String name);

    public String sayHello(String name, AsyncCallback<String> callback);

}