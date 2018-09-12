package com.freda.rpc.cluster.invoker;

import java.util.List;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.Result;
import com.freda.rpc.cluster.AbstractClusterInvoker;
import com.freda.rpc.cluster.balance.BalanceStrategy;

public class FailFastClusterInvoker<T> extends AbstractClusterInvoker<T> {

	public FailFastClusterInvoker(List<Invoker<T>> invokers) {
		super(invokers);
	}

	@Override
	protected Result doInvoker(List<Invoker<T>> invokers, RequestMessage inv, BalanceStrategy balanceStrategy) {
		Invoker<T> invoker = balanceStrategy.balance(inv, invokers);
		return invoker.invoke(inv);
	}

}