package com.freda.rpc.cluster.balance.strategy;

import java.util.List;
import java.util.TreeMap;

import com.freda.registry.Server;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.cluster.balance.BalanceStrategy;

/**
 * 一致性Hash算法
 * 
 * @author wukai
 *
 */
public class ConsistantHashBalanceStrategy implements BalanceStrategy {


	private static final class ConsistantHashSelector {

		private TreeMap<Long, Server> vitualServerMap = new TreeMap<Long, Server>();
		private int indentityHashCode;

		ConsistantHashSelector(int indentityHashCode, List<Server> servers) {
			this.indentityHashCode = indentityHashCode;
			
			for (Server server : servers) {
				for (int i = 0; i < 40; i++) {
					
				}
			}
		}
		
		
	}



	@Override
	public <T> Invoker<T> balance(RequestMessage inv, List<Invoker<T>> invokers) {
		return null;
	}

}
