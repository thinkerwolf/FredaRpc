package com.freda.rpc.cluster.invoker;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.Result;
import com.freda.rpc.cluster.AbstractClusterInvoker;
import com.freda.rpc.cluster.balance.BalanceStrategy;

import java.util.List;

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
