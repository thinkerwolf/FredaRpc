package com.freda.rpc.cluster;

import java.util.List;

import com.freda.rpc.Invoker;

public interface Cluster {

	<T> Invoker<T> combine(List<Invoker<T>> invokers);
	
}
