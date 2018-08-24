package com.freda.config.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.freda.common.conf.NetConfig;
import com.freda.config.Configuration;

public class NetBean extends NetConfig implements InitializingBean {

	private boolean server;

	@Override
	public void afterPropertiesSet() throws Exception {
		Configuration conf = Configuration.getInstance();
		if (server) {
			List<NetConfig> list = conf.getNettyServerConfigs();
			if (list == null) {
				list = new ArrayList<>();
				conf.setNettyServerConfigs(list);
			}
			if (!list.contains(this)) {
				list.add(this);
			}
		} else {
			if (Configuration.getInstance().getNettyClientConfig() == null) {
				Configuration.getInstance().setNettyClientConfig(this);
			}
		}
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

}