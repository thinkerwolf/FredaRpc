package com.freda.rpc;

public class RpcException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 5988182282253054101L;

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

}
