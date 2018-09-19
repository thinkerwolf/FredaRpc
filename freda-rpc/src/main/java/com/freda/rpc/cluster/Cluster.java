package com.freda.rpc.cluster;

import com.freda.common.SLI;
import com.freda.rpc.Invoker;

import java.util.List;
@SLI("failfast")
public interface Cluster {

    <T> Invoker<T> combine(List<Invoker<T>> invokers);

}
