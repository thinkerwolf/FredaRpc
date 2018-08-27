package com.freda.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.freda.remoting.RemotingClient;
import com.freda.remoting.RemotingServer;

public abstract class AbstractProtocol implements Protocol {

	protected Map<String, RemotingClient> remotingClientMap = new ConcurrentHashMap<>();

	protected Map<String, RemotingServer> remotingServerMap = new ConcurrentHashMap<>();
	
	protected Map<String, Invoker<?>> invokers = new ConcurrentHashMap<>();
	
	public AbstractProtocol() {
		
	}
	
}
