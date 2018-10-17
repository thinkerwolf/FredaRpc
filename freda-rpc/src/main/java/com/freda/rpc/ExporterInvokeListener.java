package com.freda.rpc;

import com.freda.common.Net;

/**
 * Exporter
 * @author wukai
 *
 */
public interface ExporterInvokeListener {
	
	void invocation(Net net, String methodName, Class<?>[] parameterTypes, Object[] parameterValues);
	
}
