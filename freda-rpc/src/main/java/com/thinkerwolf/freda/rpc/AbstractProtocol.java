package com.thinkerwolf.freda.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractProtocol implements Protocol {

    protected Map<String, Invoker<?>> invokers = new ConcurrentHashMap<>();

    public AbstractProtocol() {

    }

}
