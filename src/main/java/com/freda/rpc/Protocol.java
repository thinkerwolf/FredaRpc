package com.freda.rpc;

import java.util.List;

import com.freda.common.conf.NetConfig;

/**
 * 协议
 *
 * @author wukai
 */
public interface Protocol {
	
    String name();

    void send(Object obj);

    <T> Invoker<T> refer(Class<T> type, List<NetConfig> ncs);
    
    <T> Exporter<T> export(Class<T> type, T ref, List<NetConfig> ncs);
    
}
