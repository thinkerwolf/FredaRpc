package com.freda.rpc.http;


import com.freda.rpc.AbstractExporter;

public class HttpExporter<T> extends AbstractExporter<T> {

    public HttpExporter(String id, Class<T> type, T ref) {
        super(id, type, ref);
    }

    @Override
    public synchronized void destory() {
        if (destory) {
            return;
        }
        destory = true;
        FrameworkServlet.getInstance().removeExpoter(this);
    }

}
