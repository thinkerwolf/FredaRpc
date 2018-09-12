package com.freda.rpc.cluster;

import java.util.List;

import com.freda.rpc.Invoker;
import com.freda.rpc.cluster.invoker.FailFastClusterInvoker;

public class FailFastCluster implements Cluster {

	@Override
	public <T> Invoker<T> combine(List<Invoker<T>> invokers) {
		return new FailFastClusterInvoker<>(invokers);
	}

}
