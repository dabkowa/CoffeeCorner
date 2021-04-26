package com.swissre.coffeecorner.exception;

public class UnknownProductException extends RuntimeException {
	
	private static final long serialVersionUID = 3032663889699724281L;

	public UnknownProductException(String message) {
		super(message);
	}

}
