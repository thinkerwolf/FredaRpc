package com.freda.rpc.cluster;

import java.util.List;

import com.freda.common.Constants;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.Result;
import com.freda.rpc.RpcException;
import com.freda.rpc.cluster.balance.BalanceProcessor;
import com.freda.rpc.cluster.balance.BalanceStrategy;
import com.freda.rpc.cluster.balance.StrategyType;

public abstract class AbstractClusterInvoker<T> implements Invoker<T> {

	private List<Invoker<T>> invokers;

	public AbstractClusterInvoker(List<Invoker<T>> invokers) {
		this.invokers = invokers;
	}

	@Override
	public Result invoke(RequestMessage inv) throws RpcException {
		String balance = String.valueOf(inv.getParameter(Constants.BALANCE, StrategyType.RANDOM.getName()));
		BalanceStrategy balanceStrategy = BalanceProcessor.getInstance().getBalanceStrategy(balance);
		return doInvoker(invokers, inv, balanceStrategy);
	}

	protected abstract Result doInvoker(List<Invoker<T>> invokers, RequestMessage inv,
			BalanceStrategy balanceStrategy);

	@Override
	public Class<T> getType() {
		return invokers.get(0).getType();
	}

	@Override
	public String id() {
		return String.valueOf(this.hashCode());
	}

}
