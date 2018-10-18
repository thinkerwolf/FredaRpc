package com.thinkerwolf.freda.rpc.cluster.balance;

import java.util.List;

import com.thinkerwolf.freda.common.SLI;
import com.thinkerwolf.freda.rpc.Invoker;
import com.thinkerwolf.freda.rpc.RequestMessage;
@SLI("random")
public interface BalanceStrategy {

    <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers);

}
