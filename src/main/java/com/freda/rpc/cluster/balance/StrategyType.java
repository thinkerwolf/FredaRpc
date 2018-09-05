package com.freda.rpc.cluster.balance;

public enum StrategyType {
	
	RANDOM("random", "随机策略"),
	CONSISTANT_HASH("hash", "一致性hash策略"),
	
	;

	private String name;

	private String intro;

	private StrategyType(String name, String intro) {
		this.name = name;
		this.intro = intro;
	}

	public String getName() {
		return name;
	}

	public String getIntro() {
		return intro;
	}

}
