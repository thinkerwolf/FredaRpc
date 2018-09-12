package com.freda.remoting.web;

public class WebServerException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1375299016459049987L;

    public WebServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServerException(String message) {
        super(message);
    }

    public WebServerException(Throwable cause) {
        super(cause);
    }

}
