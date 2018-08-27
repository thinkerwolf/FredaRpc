package com.freda.remoting;

import com.freda.common.conf.NetConfig;

public abstract class RemotingClient extends AbstractRemoting {

    public RemotingClient(NetConfig conf) {
        super(conf);
    }

    public RemotingClient(NetConfig conf, RemotingHandler handler) {
        super(conf, handler);
    }

    /**
     * 同步调用
     *
     * @return
     */
    public abstract <T> T sendSync(Class<T> clazz);

    /**
     * 异步调用
     *
     * @return
     */
    public abstract void invokeAsync();

    @Override
    public ClientRemotingHandler handler() {
        return (ClientRemotingHandler) super.handler();
    }

}
