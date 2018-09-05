package com.freda.rpc.cluster.balance.strategy;

import java.util.List;

import com.freda.common.util.RandomUtil;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.cluster.balance.BalanceStrategy;
import com.freda.rpc.cluster.balance.StrategyType;

public class RandomBalanceStrategy extends BalanceStrategy {
	
	public RandomBalanceStrategy() {
		super(StrategyType.RANDOM);
	}

	@Override
	public <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers) {
		if (invokers.size() == 1) {
			return invokers.get(0);
		}
		int len = invokers.size();
		return invokers.get(RandomUtil.nextInt(len));
	}

	

}
