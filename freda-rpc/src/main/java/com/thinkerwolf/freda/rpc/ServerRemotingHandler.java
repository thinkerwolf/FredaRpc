package com.thinkerwolf.freda.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thinkerwolf.freda.common.concurrent.DefaultPromise;
import com.thinkerwolf.freda.common.concurrent.Future;
import com.thinkerwolf.freda.remoting.Channel;
import com.thinkerwolf.freda.remoting.RemotingHandler;

public class ServerRemotingHandler implements RemotingHandler {
	
    Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();
    
    private Class<?> decodeClass;
    
    private CountAwareThreadPoolExecutor executor;
    
    public ServerRemotingHandler(Class<?> decodeClass) {
		this.decodeClass = decodeClass;
		this.executor = new CountAwareThreadPoolExecutor("Server-User", 10);
	}

	@Override
    public Future<?> send(Channel channel, Object msg) {
        DefaultPromise<Object> rf = new DefaultPromise<Object>();
        channel.send(msg);
        return rf;
    }

    @Override
    public void received(Channel channel, Object msg) {
    	// modify by wukai on 2019.3.28 使用专门的业务线程池处理请求
    	ChannelRunnable cr = new ServerChannelRunnable(channel, msg, this);
    	executor.execute(cr);
    }

    public void addExporter(Exporter<?> e) {
        exporters.put(e.id(), e);
    }

    public void removeExporter(Exporter<?> e) {
        exporters.remove(e.id());
    }

	@Override
	public Class<?> decodeClass() {
		return decodeClass;
	}

}
