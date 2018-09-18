package com.freda.rpc.cluster.invoker;

import com.freda.common.Constants;
import com.freda.rpc.Invoker;
import com.freda.rpc.RequestMessage;
import com.freda.rpc.Result;
import com.freda.rpc.RpcException;
import com.freda.rpc.cluster.AbstractClusterInvoker;
import com.freda.rpc.cluster.balance.BalanceStrategy;

import java.util.List;

/**
 * 失败重试
 *
 * @param <T>
 * @author wukai
 */
public class FailOverClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public FailOverClusterInvoker(List<Invoker<T>> invokers) {
        super(invokers);
    }

    @Override
    protected Result doInvoker(List<Invoker<T>> invokers, RequestMessage inv, BalanceStrategy balanceStrategy, boolean isAsync) {
        int retries = (int) inv.getParameter(Constants.RETRIES, Constants.DEFAULT_RETRY_TIMES) + 1;
        for (int i = 0; i < retries; i++) {
            Invoker<T> invoker = balanceStrategy.balance(inv, invokers);
            Result result = invoker.invoke(inv, isAsync);
            if (result.isSuccess()) {
                return result;
            }
        }
        throw new RpcException("............ cluster invoker fail ");
    }

}
