package com.freda.rpc;

import java.util.List;

import com.freda.common.Net;

/**
 * 协议
 *
 * @author wukai
 */
public interface Protocol {
	
    String name();

    <T> Invoker<T> refer(String id, Class<T> type, List<Net> ncs);
    
    <T> Exporter<T> export(String id, Class<T> type, T ref, Net nc);
    
}
