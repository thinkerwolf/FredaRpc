package com.thinkerwolf.freda.rpc.cluster;

import java.util.List;

import com.thinkerwolf.freda.common.Constants;
import com.thinkerwolf.freda.common.ServiceLoader;
import com.thinkerwolf.freda.rpc.Invoker;
import com.thinkerwolf.freda.rpc.RequestMessage;
import com.thinkerwolf.freda.rpc.Result;
import com.thinkerwolf.freda.rpc.RpcException;
import com.thinkerwolf.freda.rpc.cluster.balance.BalanceStrategy;

public abstract class AbstractClusterInvoker<T> implements Invoker<T> {

    private List<Invoker<T>> invokers;

    public AbstractClusterInvoker(List<Invoker<T>> invokers) {
        this.invokers = invokers;
    }

    @Override
    public Result invoke(RequestMessage inv) throws RpcException {
        return invoke(inv, false);
    }

    @Override
    public Result invoke(RequestMessage inv, boolean isAsync) throws RpcException {
        String balance = String.valueOf(inv.getParameter(Constants.BALANCE, Constants.DEFAULT_BALANCE_TYPE));
        BalanceStrategy balanceStrategy = ServiceLoader.getService(balance, BalanceStrategy.class);
        return doInvoker(invokers, inv, balanceStrategy, isAsync);
    }

    protected abstract Result doInvoker(List<Invoker<T>> invokers, RequestMessage inv,
                                        BalanceStrategy balanceStrategy, boolean isAsync);

    @Override
    public Class<T> getType() {
        return invokers.get(0).getType();
    }

    @Override
    public String id() {
        return String.valueOf(this.hashCode());
    }


    @Override
    public void destory() {
        for (Invoker<T> invoker : invokers) {
            invoker.destory();
        }
    }
}
