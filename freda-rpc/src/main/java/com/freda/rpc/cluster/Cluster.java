package com.freda.rpc.cluster;

import com.freda.rpc.Invoker;

import java.util.List;

public interface Cluster {

    <T> Invoker<T> combine(List<Invoker<T>> invokers);

}
