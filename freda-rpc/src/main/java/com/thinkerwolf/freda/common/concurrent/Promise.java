package com.thinkerwolf.freda.common.concurrent;

public interface Promise<V> {

    void setSuccess(V result);

    void setFailure(Throwable cause);
}
