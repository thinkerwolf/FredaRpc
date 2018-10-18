package com.thinkerwolf.freda.rpc.cluster.balance.strategy;

import java.util.List;

import com.thinkerwolf.freda.common.util.RandomUtil;
import com.thinkerwolf.freda.rpc.Invoker;
import com.thinkerwolf.freda.rpc.RequestMessage;
import com.thinkerwolf.freda.rpc.cluster.balance.BalanceStrategy;

public class RandomBalanceStrategy implements BalanceStrategy {


    @Override
    public <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers) {
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        int len = invokers.size();
        return invokers.get(RandomUtil.nextInt(len));
    }


}
