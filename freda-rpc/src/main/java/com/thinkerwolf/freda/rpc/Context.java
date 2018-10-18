package com.thinkerwolf.freda.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thinkerwolf.freda.common.concurrent.Future;

/**
 * @author wukai
 */
public final class Context {

	private static final ThreadLocal<Context> LOCAL = new ThreadLocal<Context>() {
		protected Context initialValue() {
			return new Context();
		}
	};
	/**
	 * current request message
	 */
	private RequestMessage current;
	private Map<RequestMessage, Future<?>> futureMap = new ConcurrentHashMap<>(20);

	private Context() {
	}

	public static Context getContext() {
		return LOCAL.get();
	}

	public Future<?> setCurrent(RequestMessage current, Future<?> future) {
		this.current = current;
		return futureMap.putIfAbsent(current, future);
	}

	public Future<?> getFuture() {
		return futureMap.get(current);
	}

	public Future<?> getFuture(RequestMessage inv) {
		return futureMap.get(inv);
	}

}
