package com.freda.rpc.cluster.balance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.freda.rpc.cluster.balance.strategy.RandomBalanceStrategy;


public class BalanceProcessor {

	private static final BalanceProcessor INSTANCE = new BalanceProcessor();

	public static BalanceProcessor getInstance() {
		return INSTANCE;
	}

	private Map<String, BalanceStrategy> strategyMap = new ConcurrentHashMap<>();

	private BalanceProcessor() {
		init();
	}

	private void init() {
		strategyMap.put(StrategyType.RANDOM.getName(), new RandomBalanceStrategy());
	}

	public BalanceStrategy getBalanceStrategy(String name) {
		BalanceStrategy bs = strategyMap.get(name);
		return bs == null ? getDefaultBalanceStrategy() : bs;
	}

	public BalanceStrategy getDefaultBalanceStrategy() {
		BalanceStrategy bs = strategyMap.get(StrategyType.RANDOM.getName());
		if (bs == null) {
			bs = new RandomBalanceStrategy();
			strategyMap.put(StrategyType.RANDOM.getName(), bs);
		}
		return bs;
	}

}
