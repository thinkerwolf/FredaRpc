package com.freda.remoting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.freda.config.NettyConfig;
import com.freda.config.ReferenceConfig;

public abstract class RemotingClient extends AbstractRemoting {

	protected ConcurrentMap<String, ReferenceConfig<?>> referenceMap = new ConcurrentHashMap<>();

	public RemotingClient(NettyConfig conf) {
		super(conf);
	}

	@SuppressWarnings("unchecked")
	public <T> ReferenceConfig<T> getReferenceConfig(Class<T> clazz) {
		for (ReferenceConfig<?> rc : referenceMap.values()) {
			if (clazz.equals(rc.getInterfaceClass())) {
				return (ReferenceConfig<T>) rc;
			}
		}
		return null;
	}

	public ReferenceConfig<?> getReferenceConfig(String id) {
		return referenceMap.get(id);
	}

	public void addReferenceConfig(ReferenceConfig<?> reConfig) {
		referenceMap.put(reConfig.getId(), reConfig);
	}

	/**
	 * 同步调用
	 * 
	 * @return
	 */
	public abstract <T> T invokeSync(Class<T> clazz);

	/**
	 * 异步调用
	 * 
	 * @return
	 */
	public abstract void invokeAsync();

}
