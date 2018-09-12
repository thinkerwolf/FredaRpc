package com.freda.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.freda.common.Constants;
import com.freda.common.Net;
import com.freda.common.ServiceLoader;
import com.freda.common.proxy.ProxyHandler;
import com.freda.common.util.ProxyUtils;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.remoting.RequestMessage;
import com.freda.rpc.Invoker;
import com.freda.rpc.Protocol;
import com.freda.rpc.cluster.Cluster;

/**
 * 调用者配置
 *
 * @param <T>
 * @author wukai
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {

	private Invoker<T> invoker;

	private List<ClientConfig> clientConfs;

	protected String cluster = "failfast";

	protected int retries = Constants.DEFAULT_RETRY_TIMES;

	protected String balance = "random";

	/** client ids {client1,client2} */
	protected String clients;

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getClients() {
		return clients;
	}

	public void setClients(String clients) {
		this.clients = clients;
	}

	public List<ClientConfig> getClientConfs() {
		return clientConfs;
	}

	public void setClientConfs(List<ClientConfig> clientConfs) {
		this.clientConfs = clientConfs;
	}

	@Override
	public void export() throws Exception {
		// 暴露
		List<Registry> registries = conf.handleRegistries(this.registryConfs);
		
		/** mulitple clients */
		if (clientConfs == null || clientConfs.size() == 0) {
			throw new ReferenceConfigException("lack client config");
		}

		List<Invoker<T>> invokers = new LinkedList<>();
		for (ClientConfig cc : clientConfs) {
			if (cc.isUsable()) {
				Protocol protocol = ServiceLoader.getService(cc.getProtocol(), Protocol.class);
				List<Net> ncs = new ArrayList<>(1);
				ncs.add(new Net(cc.getHost(), cc.getPort(), cc.getProtocol()));
				invokers.add(protocol.refer(getId(), getInterfaceClass(), ncs));
			} else {
				Protocol protocol = ServiceLoader.getService(cc.getProtocol(), Protocol.class);
				for (Registry registry : registries) {
					List<Net> ncs = getNetConfs(registry, cc);
					if (ncs != null && ncs.size() > 0) {
						invokers.add(protocol.refer(getId(), getInterfaceClass(), ncs));
					}
				}
			}
		}

		if (invokers.size() == 0) {
			throw new ReferenceConfigException("can't refer obtain from registry, please check the registry");
		}
		this.cluster = StringUtils.isEmpty(this.cluster) ? Constants.DEFAULT_CLUSTER_TYPE : this.cluster;
		this.invoker = ServiceLoader.getService(cluster, Cluster.class).combine(invokers);
	}

	private List<Net> getNetConfs(Registry registry, ClientConfig cc) {
		List<Server> servers = registry.getServersByProtocol(cc.getProtocol());
		if (servers == null || servers.size() == 0) {
			return null;
		}
		List<Net> netConfs = new LinkedList<>();
		for (Server s : servers) {
			Net nc = new Net();
			nc.setHost(s.getHost());
			nc.setPort(s.getPort());
			nc.setProtocol(s.getProtocol());
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
							final ReferenceConfig<T> rc = ReferenceConfig.this;
							RequestMessage r = new RequestMessage();
							r.setArgs(args);
							r.setMethodName(method.getName());
							r.setId(genId());
							r.setClazzName(getId());
							r.setParameterTypes(method.getParameterTypes());
							r.putParameter(Constants.RETRIES, rc.getRetries());
							r.putParameter(Constants.BALANCE, rc.getBalance());
							return rc.getInvoker().invoke(r).getValue();
						}
					});
					ref = ((T) obj);
				}
			}
		}
		return ref;
	}

}
