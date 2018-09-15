package com.freda.rpc;

import com.freda.common.Net;

import java.util.List;

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
