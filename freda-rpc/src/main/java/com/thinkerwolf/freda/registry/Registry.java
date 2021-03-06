package com.thinkerwolf.freda.registry;

import java.util.List;

import com.thinkerwolf.freda.common.Net;

/**
 * @author wukai
 */
public interface Registry {
    /**
     * 注册服务器
     *
     * @param server
     * @throws Exception
     */
    void register(Server server) throws Exception;

    /**
     * 注销服务器
     *
     * @param server
     * @throws Exception
     */
    void unregister(Server server) throws Exception;

    /**
     * 选择一台服务器
     *
     * @return
     * @throws Exception
     */
    Server getRandomServer(String protocol) throws Exception;

    List<Server> getServersByProtocol(String protocol);

    void start() throws Exception;

    void close() throws Exception;

    boolean isConnected();

    Net getNet();

}
