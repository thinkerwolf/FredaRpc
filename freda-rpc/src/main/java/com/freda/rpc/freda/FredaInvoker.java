package com.freda.rpc.freda;

import com.freda.common.concurrent.DefaultPromise;
import com.freda.common.concurrent.Future;
import com.freda.common.concurrent.FutureListener;
import com.freda.remoting.RemotingClient;
import com.freda.rpc.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class FredaInvoker<T> extends AbstractInvoker<T> {

    private RemotingClient[] clients;

    private AtomicInteger round = new AtomicInteger(0);

    public FredaInvoker(String id, Class<T> type, RemotingClient[] clients) {
        super(id, type);
        this.clients = clients;
    }

    @Override
    public Result invoke(RequestMessage inv) throws RpcException {
        return invoke(inv, false);
    }

    @Override
    public Result invoke(final RequestMessage inv, final boolean isAsync) throws RpcException {
        RemotingClient client = null;
        if (clients.length == 1) {
            client = clients[0];
        } else {
            client = clients[Math.abs(round.getAndIncrement() % clients.length)];
        }
        Future<?> rf = client.handler().send(client.channel(), inv);

        if (isAsync) {
            Context context = Context.getContext();
            final DefaultPromise<Object> promise = new DefaultPromise<>();
            context.setCurrent(inv, promise);
            rf.addListener(new FutureListener<Future<Object>>() {
                @Override
                public void operationComplete(Future<Object> future) throws Throwable {
                    if (future.isSuccess()) {
                        promise.setSuccess(future.get());
                    } else {
                        promise.setFailure(future.cause());
                    }
                }
            });
            return ResultBuilder.buildSuccessResult(null);
        } else {
            try {
                rf.sync();
                if (rf.isSuccess()) {
                    return ResultBuilder.buildSuccessResult(rf.get());
                } else {
                    return ResultBuilder.buildFailResult();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RpcException("Rpc future sync exception", e);
            }

        }
    }

    @Override
    public synchronized void destory() {
        if (destory) {
            return;
        }
        destory = true;
        if (clients != null) {
            for (int i = 0; i < clients.length; i++) {
                clients[i] = null;
            }
            clients = null;
        }
    }


}
