package com.freda.rpc;

import com.freda.common.concurrent.Future;
import com.freda.remoting.RequestMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wukai
 */
public final class Context {

    private static final ThreadLocal<Context> LOCAL = ThreadLocal.withInitial(() -> new Context());
    /**
     * current request message
     */
    private RequestMessage current;
    private Map<RequestMessage, Future<?>> futureMap = new ConcurrentHashMap<>(20);

    private Context() {
    }

    public static Context getContext() {
        return LOCAL.get();
    }

    public Future<?> setCurrent(RequestMessage current, Future<?> future) {
        this.current = current;
        return futureMap.putIfAbsent(current, future);
    }


    public Future<?> getFuture() {
        return futureMap.get(current);
    }

    public Future<?> getFuture(RequestMessage inv) {
        return futureMap.get(inv);
    }

}
