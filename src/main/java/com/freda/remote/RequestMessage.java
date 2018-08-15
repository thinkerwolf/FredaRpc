package com.freda.remote;

import java.io.Serializable;
import java.util.Arrays;

public class RequestMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5906021628390669682L;


    int id;

    String clazzName;
    String methodName;
    Object[] args;
    Class<?>[] parameterTypes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args == null ? new Object[]{} : args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes == null ? new Class<?>[]{} : parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("clazzName#" + clazzName + " ,args#" + Arrays.toString(args));
        sb.append(" ,method#" + methodName);
        return sb.toString();
    }

}
