package com.freda.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.freda.remoting.RequestMessage;

/**
 * 
 * @author wukai
 *
 */
public final class Context {

	private Context() {
	}

	private static final ThreadLocal<Context> LOCAL = new ThreadLocal<Context>() {
		protected Context initialValue() {
			return new Context();
		}
	};

	public static Context getContext() {
		return LOCAL.get();
	}

	/** current request message */
	private RequestMessage current;

	private Map<RequestMessage, AsyncCallback<?>> callbacks = new ConcurrentHashMap<>(256);
	
	
	public void setCurrent(RequestMessage current) {
		this.current = current;
		
	}
	

	public AsyncCallback<?> addCallback(RequestMessage current, AsyncCallback<?> callback) {
		this.current = current;
		return callbacks.put(current, callback);
	}
}
