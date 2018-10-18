package com.thinkerwolf.freda.rpc.cluster.invoker;

import java.util.List;

import com.thinkerwolf.freda.rpc.Invoker;
import com.thinkerwolf.freda.rpc.RequestMessage;
import com.thinkerwolf.freda.rpc.Result;
import com.thinkerwolf.freda.rpc.cluster.AbstractClusterInvoker;
import com.thinkerwolf.freda.rpc.cluster.balance.BalanceStrategy;

public class FailFastClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public FailFastClusterInvoker(List<Invoker<T>> invokers) {
        super(invokers);
    }

    @Override
    protected Result doInvoker(List<Invoker<T>> invokers, RequestMessage inv, BalanceStrategy balanceStrategy, boolean isAsync) {
        Invoker<T> invoker = balanceStrategy.balance(inv, invokers);
        return invoker.invoke(inv, isAsync);
    }

}
