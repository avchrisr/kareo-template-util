package com.chrisr.template_util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgumentException extends RuntimeException {

	private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

	public IllegalArgumentException(String message) {
		super(message);
	}

	public IllegalArgumentException(Throwable cause) {
		super(cause);
	}

	public IllegalArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
