package com.thinkerwolf.freda.rpc;

import com.thinkerwolf.freda.common.Net;

/**
 * Exporter
 * @author wukai
 *
 */
public interface ExporterInvokeListener {
	
	void invocation(Net net, String methodName, Class<?>[] parameterTypes, Object[] parameterValues);
	
}
