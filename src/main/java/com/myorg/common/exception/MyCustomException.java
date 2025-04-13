package com.myorg.common.exception;

public class MyCustomException extends RuntimeException {

	private static final long serialVersionUID = 122111L;

	public MyCustomException() {
        super();
    }

    public MyCustomException(String message) {
        super(message);
    }

    public MyCustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyCustomException(Throwable cause) {
        super(cause);
    }
}
