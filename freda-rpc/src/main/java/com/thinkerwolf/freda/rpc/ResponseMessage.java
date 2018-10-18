package com.thinkerwolf.freda.rpc;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5720165296614412386L;
    private boolean success;
    private Object result;
    private int requestId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setId(int id) {
        this.requestId = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
