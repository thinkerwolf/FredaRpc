package com.freda.rpc;

public class Result {
    /**
     * 网络请求成功
     */
    private boolean success;
    /**
     * 网络请求结果
     */
    private Object value;

    public Result(boolean success, Object value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object result) {
        this.value = result;
    }

}
