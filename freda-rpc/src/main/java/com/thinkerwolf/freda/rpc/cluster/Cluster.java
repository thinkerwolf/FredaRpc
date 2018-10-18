package com.thinkerwolf.freda.rpc.cluster;

import java.util.List;

import com.thinkerwolf.freda.common.SLI;
import com.thinkerwolf.freda.rpc.Invoker;
@SLI("failfast")
public interface Cluster {

    <T> Invoker<T> combine(List<Invoker<T>> invokers);

}
