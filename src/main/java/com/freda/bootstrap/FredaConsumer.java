package com.freda.bootstrap;

import com.freda.common.conf.Configuration;
import com.freda.remoting.Remoting;
import com.freda.remoting.RemotingClient;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FredaConsumer {
	private static final Logger logger = LoggerFactory.getLogger(FredaProvidor.class);
	private Configuration conf;
	private String confFilePath;

	private List<RemotingClient> remotes;

	public FredaConsumer() {

	}

	public void setConfFilePath(String confFilePath) {
		this.confFilePath = confFilePath;
	}

	public void start() {
		try {
			if (confFilePath == null) {
				this.conf = Configuration.newConfiguration();
			} else {
				this.conf = Configuration.newConfiguration(confFilePath);
			}
			this.remotes = new ArrayList<>();
			for (Remoting r : this.conf.getRemotings()) {
				if (r instanceof RemotingClient) {
					remotes.add((RemotingClient) r);
				}
			}

		} catch (Exception e) {
			logger.error("FredaProvidor init error", e);
		}
	}

	public <T> T refer(Class<T> clazz) {
		return remotes.get(0).invokeSync(clazz);
	}

}
