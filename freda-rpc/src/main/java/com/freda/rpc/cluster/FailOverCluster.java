package com.freda.rpc.cluster;

import com.freda.rpc.Invoker;
import com.freda.rpc.cluster.invoker.FailOverClusterInvoker;

import java.util.List;

public class FailOverCluster implements Cluster {

    @Override
    public <T> Invoker<T> combine(List<Invoker<T>> invokers) {
        return new FailOverClusterInvoker<>(invokers);
    }

}
