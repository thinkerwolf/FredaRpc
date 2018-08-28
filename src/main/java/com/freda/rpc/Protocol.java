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

    <T> Invoker<T> refer(String id, Class<T> type, List<NetConfig> ncs);
    
    <T> Exporter<T> export(String id, Class<T> type, T ref, NetConfig nc);
    
}
