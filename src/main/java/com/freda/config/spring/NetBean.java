package com.freda.config.spring;

import com.freda.common.conf.NetConfig;
import com.freda.config.Configuration;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

public class NetBean extends NetConfig implements InitializingBean {

	private boolean server;

	@Override
	public void afterPropertiesSet() throws Exception {
		Configuration conf = Configuration.getInstance();
		if (server) {
			List<NetConfig> list = conf.getNetServerConfigs();
			if (list == null) {
				list = new ArrayList<>();
				conf.setNetServerConfigs(list);
			}
			if (!list.contains(this)) {
				list.add(this);
			}
		} else {
			if (Configuration.getInstance().getNetClientConfig() == null) {
				Configuration.getInstance().setNetClientConfig(this);
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
