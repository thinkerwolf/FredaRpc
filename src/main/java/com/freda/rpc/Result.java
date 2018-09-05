package com.freda.rpc;

public class Result {

	private boolean success;

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
