package com.thinkerwolf.freda.remoting;

public class RemotingException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -5474494158017907388L;

    public RemotingException() {
        super();
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(Throwable cause) {
        super(cause);
    }

}
