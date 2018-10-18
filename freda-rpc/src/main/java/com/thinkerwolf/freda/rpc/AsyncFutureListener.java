package com.thinkerwolf.freda.rpc;

import com.thinkerwolf.freda.common.concurrent.Future;
import com.thinkerwolf.freda.common.concurrent.FutureListener;

public interface AsyncFutureListener<V> extends FutureListener<Future<V>> {


}
