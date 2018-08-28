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
import com.freda.rpc.ProtocolFactory;

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
			this.netConf = netConf.clone();
			Server server = getRegistrisServer();
			if (server == null) {
				throw new ReferenceConfigException(
						"can't refer reference config of [" + getInterface() + "], please check the config file");
			}
			netConf.setIp(server.getHost());
			netConf.setPort(server.getPort());
		}
		Protocol protocol = ProtocolFactory.getProtocolByName(netConf.getProtocol());
		List<NetConfig> ncs = new ArrayList<>(1);
		ncs.add(netConf);
		this.invoker = protocol.refer(getId(), getInterfaceClass(), ncs);
	}

	private Server getRegistrisServer() {
		if (registries.size() == 0) {
			return null;
		}
		for (Registry r : registries) {
			try {
				Server server = r.getRandomServer(netConf.getProtocol());
				if (server != null) {
					return server;
				}
			} catch (Exception e) {
				// ignore
				continue;
			}
		}
		return null;
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
							return ReferenceConfig.this.getInvoker().invoke(r);
						}
					});
					ref = ((T) obj);
				}
			}
		}
		return ref;
	}

}
