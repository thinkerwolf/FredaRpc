package com.freda.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.freda.common.conf.NetConfig;
import com.freda.common.proxy.ProxyHandler;
import com.freda.common.util.ProxyUtils;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.Protocol;
import com.freda.rpc.ProtocolLoader;
import com.freda.rpc.cluster.ClusterLoader;

/**
 * 调用者配置
 *
 * @param <T>
 * @author wukai
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {

	private NetConfig netConf;

	private Invoker<T> invoker;

	private List<Registry> registries;
	
	public NetConfig getNetConfig() {
		return netConf;
	}

	public void setNetConf(NetConfig nettyConf) {
		this.netConf = nettyConf;
	}

	@Override
	public void export() throws Exception {
		// 暴露
		this.registries = conf.handleRegistries(this.registryConfs);
		if (!this.netConf.isUseable()) {
			if (registries.size() <= 0) {
				throw new ReferenceConfigException(
						"can't refer reference config of [" + getInterface() + "], please check the config file");
			}
			Protocol protocol = ProtocolLoader.getProtocolByName(netConf.getProtocol());
			List<Invoker<T>> invokers = new ArrayList<>();
			for (Registry registry : registries) {
				List<NetConfig> ncs = getNetConfs(registry);
				if (ncs != null && ncs.size() > 0) {
					invokers.add(protocol.refer(getId(), getInterfaceClass(), ncs));
				}
			}
			// 集群选择 
			this.invoker = ClusterLoader.getClusterByName("failover").combine(invokers);
		} else {
			Protocol protocol = ProtocolLoader.getProtocolByName(netConf.getProtocol());
			List<NetConfig> ncs = new ArrayList<>(1);
			ncs.add(netConf);
			this.invoker = protocol.refer(getId(), getInterfaceClass(), ncs);
		}
		
	}
	
	private List<NetConfig> getNetConfs(Registry registry) {
		List<Server> servers = registry.getServersByProtocol(netConf.getProtocol());
		if (servers == null || servers.size() == 0) {
			return null;
		}
		List<NetConfig> netConfs = new ArrayList<>(servers.size());
		for (Server s : servers) {
			NetConfig nc = new NetConfig();
			nc.setIp(s.getHost());
			nc.setPort(s.getPort());
			nc.setProtocol(s.getProtocol());
			nc.setBossThreads(netConf.getBossThreads());
			nc.setWorkerThreads(netConf.getWorkerThreads());
			netConfs.add(nc);
		}
		return netConfs;
	}
	
	@Override
	public void unexport() throws Exception {

	}

	public Invoker<T> getInvoker() {
		return invoker;
	}

	private static AtomicInteger ID_GEN = new AtomicInteger(1);

	public static int genId() {
		return ID_GEN.getAndIncrement();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getRef() {
		if (ref == null) {
			synchronized (this) {
				if (ref == null) {
					Object obj = ProxyUtils.newProxy(getInterfaceClass(), new ProxyHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							RequestMessage r = new RequestMessage();
							r.setArgs(args);
							r.setMethodName(method.getName());
							r.setId(genId());
							r.setClazzName(getId());
							r.setParameterTypes(method.getParameterTypes());
							return ReferenceConfig.this.getInvoker().invoke(r).getValue();
						}
					});
					ref = ((T) obj);
				}
			}
		}
		return ref;
	}

}
