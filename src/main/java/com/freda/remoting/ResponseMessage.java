package com.freda.remoting;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5720165296614412386L;
    private boolean success;
    private Object result;
    private int id;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
