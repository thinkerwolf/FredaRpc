package com.freda.common;

public interface Constants {
	/** default fail retry time */
	public static final int DEFAULT_RETRY_TIMES = 2;
	/** default connnect timeout (ms) */
	public static final int DEFAULT_TIMEOUT = 1000;
	
	/** parameter key of retry times */
	public static final String RETRIES = "retries";
	/** parameter key of balance name */
	public static final String BALANCE = "balance";
	/** parameter key of timeout */
	public static final String TIMEOUT = "timeout";
}
