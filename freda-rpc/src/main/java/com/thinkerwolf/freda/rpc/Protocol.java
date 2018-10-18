package com.thinkerwolf.freda.rpc;

import java.util.List;

import com.thinkerwolf.freda.common.Net;
import com.thinkerwolf.freda.common.SLI;

/**
 * 协议
 *
 * @author wukai
 */
@SLI("freda")
public interface Protocol {

    String name();

    <T> Invoker<T> refer(String id, Class<T> type, List<Net> ncs);

    <T> Exporter<T> export(String id, Class<T> type, T ref, Net nc);

}
