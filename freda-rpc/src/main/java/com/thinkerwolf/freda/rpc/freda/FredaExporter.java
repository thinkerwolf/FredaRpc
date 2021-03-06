package com.thinkerwolf.freda.rpc.freda;

import com.thinkerwolf.freda.rpc.AbstractExporter;
import com.thinkerwolf.freda.rpc.ServerRemotingHandler;

public class FredaExporter<T> extends AbstractExporter<T> {

    protected ServerRemotingHandler handler;

    public FredaExporter(String id, Class<T> type, T ref, ServerRemotingHandler handler) {
        super(id, type, ref);
        this.handler = handler;
    }

    @Override
    public synchronized void destory() {
        if (destory) {
            return;
        }
        destory = true;
        handler.removeExporter(this);
    }

}
