package com.freda.config;

import com.freda.common.conf.NetConfig;
import com.freda.remoting.RemotingClient;

/**
 * 调用者配置
 *
 * @param <T>
 * @author wukai
 */
public class ReferenceConfig<T> extends InterfaceConfig<T> {

    private NetConfig nettyConf;

    public NetConfig getNettyConfig() {
        return nettyConf;
    }

    public void setNettyConf(NetConfig nettyConf) {
        this.nettyConf = nettyConf;
    }

    @Override
    public void export() {
        conf.addReferenceConfig(this);
    }

    @Override
    public void unexport() {
        RemotingClient rc = conf.getRemotingClient(nettyConf);
        rc.handler().removeReferenceConfig(this);
    }

}
