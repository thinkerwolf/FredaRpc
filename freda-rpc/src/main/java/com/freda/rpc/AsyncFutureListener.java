package com.freda.rpc;

import com.freda.common.concurrent.Future;
import com.freda.common.concurrent.FutureListener;

public interface AsyncFutureListener<V> extends FutureListener<Future<V>> {


}
