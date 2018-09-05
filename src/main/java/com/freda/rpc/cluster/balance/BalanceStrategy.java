package com.freda.rpc.cluster.balance;

import java.util.List;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;

public abstract class BalanceStrategy {

	protected StrategyType strategyType;

	public BalanceStrategy(StrategyType strategyType) {
		this.strategyType = strategyType;
	}

	public String getStrategyName() {
		return strategyType.getName();
	}

	public StrategyType getStrategyType() {
		return strategyType;
	}

	public abstract <T>  Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers);

}
