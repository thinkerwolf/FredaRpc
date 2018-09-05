package com.freda.rpc.cluster;

import java.util.List;

import com.freda.rpc.Invoker;
import com.freda.rpc.cluster.invoker.FailOverClusterInvoker;

public class FailOverCluster implements Cluster {

	@Override
	public <T> Invoker<T> combine(List<Invoker<T>> invokers) {
		return new FailOverClusterInvoker<>(invokers);
	}

}
