package com.thinkerwolf.freda.rpc;

import com.thinkerwolf.freda.remoting.Channel;

public abstract class ChannelRunnable implements Runnable {

	protected Channel channel;
	protected Object msg;

	public ChannelRunnable(Channel channel, Object msg) {
		this.channel = channel;
		this.msg = msg;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

}
