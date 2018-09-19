package com.freda.config;

import org.apache.commons.lang3.StringUtils;

/**
 * client config
 *
 * @author wukai
 */
public class ClientConfig extends AbstractNetworkConfig {

	private static final long serialVersionUID = 6587596258102528034L;

	public boolean isUsable() {
		if (StringUtils.isNotEmpty(host) && port > 0 && StringUtils.isNotEmpty(protocol)) {
			return true;
		}
		return false;
	}
	
}
