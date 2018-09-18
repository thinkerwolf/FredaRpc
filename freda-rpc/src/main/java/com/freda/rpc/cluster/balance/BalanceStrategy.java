package com.freda.rpc.cluster.balance;

import com.freda.rpc.Invoker;
import com.freda.rpc.RequestMessage;

import java.util.List;

public interface BalanceStrategy {

    <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers);

}
