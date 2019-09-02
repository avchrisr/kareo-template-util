package com.chrisr.template_util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {

	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	public AppException(String message) {
		super(message);
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
