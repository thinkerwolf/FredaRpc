package com.thinkerwolf.freda.rpc;

public abstract class AbstractInvoker<T> implements Invoker<T> {
    protected volatile boolean destory;
    private Class<T> type;
    private String id;

    public AbstractInvoker(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String id() {
        return id;
    }

}
