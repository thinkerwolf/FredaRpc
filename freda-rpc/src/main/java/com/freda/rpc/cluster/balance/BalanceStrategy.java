package com.freda.rpc.cluster.balance;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;

import java.util.List;

public interface BalanceStrategy {

    <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers);

}
