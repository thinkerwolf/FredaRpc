package com.freda.config;

import com.freda.common.Constants;
import com.freda.common.Net;
import com.freda.common.ServiceLoader;
import com.freda.common.proxy.ProxyHandler;
import com.freda.common.util.ProxyUtils;
import com.freda.registry.Registry;
import com.freda.registry.Server;
import com.freda.rpc.Invoker;
import com.freda.rpc.Protocol;
import com.freda.rpc.RequestMessage;
import com.freda.rpc.Result;
import com.freda.rpc.cluster.Cluster;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调用者配置
 *
 * @param <T>
 * @author wukai
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {
	private static final long serialVersionUID = 1076932816528937347L;
	private static AtomicInteger ID_GEN = new AtomicInteger(1);
	protected String cluster = "failfast";
	protected int retries = Constants.DEFAULT_RETRY_TIMES;
	protected String balance = "random";
	/**
	 * client ids {client1,client2}
	 */
	protected String clients;
	/**
	 * registry centers
	 */
	protected String registries;
	/**
	 * method invoke async
	 */
	protected boolean async;
	private Invoker<T> invoker;
	private List<ClientConfig> clientConfigs;

	public static int genId() {
		return ID_GEN.getAndIncrement();
	}

	public String getRegistries() {
		return registries;
	}

	public void setRegistries(String registries) {
		this.registries = registries;
	}

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

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setClientConfigs(List<ClientConfig> clientConfigs) {
		this.clientConfigs = clientConfigs;
	}

	@Override
	public synchronized void export() throws Exception {
		if (initialized) {
			return;
		}
		initialized = true;
		List<Registry> registries = conf.handleRegistries(this.registryConfs);
		/** mulitple clients */
		if (clientConfigs == null || clientConfigs.size() == 0) {
			throw new ReferenceConfigException("lack client config");
		}

		List<Invoker<T>> invokers = new ArrayList<>();
		for (ClientConfig cc : clientConfigs) {
			if (cc.isUsable()) {
				Protocol protocol = ServiceLoader.getService(cc.getProtocol(), Protocol.class);
				List<Net> ncs = new ArrayList<>(1);
				ncs.add(cc.getNet());
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
			Net nc = new Net(s.getHost(), s.getPort(), s.getProtocol(), cc.getTimeout(), cc.getSerialization());
			netConfs.add(nc);
		}
		return netConfs;
	}

	@Override
	public synchronized void unexport() throws Exception {
		// unexport reference config
		if (destory) {
			return;
		}
		destory = true;
		if (this.invoker != null) {
			this.invoker.destory();
		}
	}

	public Invoker<T> getInvoker() {
		return invoker;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized T getRef() {
		if (destory) {
			throw new IllegalStateException("Reference destroyed!");
		}
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					try {
						export();
					} catch (Exception e) {
						throw new ReferenceConfigException("Reference initial error", e);
					}
				}
			}
		}
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
							r.setRequestId(genId());
							r.setClazzName(getId());
							r.setParameterTypes(method.getParameterTypes());
							r.putParameter(Constants.RETRIES, rc.getRetries());
							r.putParameter(Constants.BALANCE, rc.getBalance());

							boolean isAsync = rc.isAsync();
							Result result = rc.getInvoker().invoke(r, isAsync);
							return result == null ? null : result.getValue();
						}
					});
					ref = ((T) obj);
				}
			}
		}
		return ref;
	}

}
