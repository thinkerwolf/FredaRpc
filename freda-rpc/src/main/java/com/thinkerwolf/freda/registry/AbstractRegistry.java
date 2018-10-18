package com.thinkerwolf.freda.registry;

import com.thinkerwolf.freda.common.Net;

public abstract class AbstractRegistry implements Registry {

    protected Net net;

    private boolean connected;

    public AbstractRegistry(Net net) {
        this.net = net;
    }

    @Override
    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public Net getNet() {
        return net;
    }

}
