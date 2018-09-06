package com.freda.rpc.cluster.balance;

import java.util.List;

import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;

public interface BalanceStrategy {

	<T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers);

}
