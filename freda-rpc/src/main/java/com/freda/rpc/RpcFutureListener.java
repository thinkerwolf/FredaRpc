package com.freda.rpc;

import com.freda.common.concurrent.FutureListener;

public interface RpcFutureListener {
	
	void onSuccess(Object result);
	
	void onFailure();
	
}
