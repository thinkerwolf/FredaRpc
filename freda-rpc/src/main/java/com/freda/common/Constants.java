package com.freda.common;

public interface Constants {
	/** Default fail retry time */
	public static final int DEFAULT_RETRY_TIMES = 2;
	/** Default connnect timeout (ms) */
	public static final int DEFAULT_TIMEOUT = 1000;
	/** Default balance algorithm */
	public static final String DEFAULT_BALANCE_TYPE = "random";
	/** Default consistant hash balance node num */
	public static final int DEFAULT_NODE_NUM = 160;
	/** Default cluster type */
	public static final String DEFAULT_CLUSTER_TYPE = "failfast";
	/** Defalut thread num */
	public static final int DEFAULT_THREAD_NUM = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);
	
	// =====================================
	/** Parameter key of retry times */
	public static final String RETRIES = "retries";
	/** Parameter key of balance name */
	public static final String BALANCE = "balance";
	/** Parameter key of timeout */
	public static final String TIMEOUT = "timeout";
	/** Consistant hash balance node num */
	public static final String NODE_NUM = "node.num";
	
	
	
}
