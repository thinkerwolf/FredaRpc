package com.thinkerwolf.freda.rpc;

public class ResultBuilder {

    public static Result buildSuccessResult(Object value) {
        return new Result(true, value);
    }

    public static Result buildFailResult() {
        return new Result(false, null);
    }
}
