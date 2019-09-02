package com.chrisr.template_util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException{

    private HttpStatus httpStatus = HttpStatus.CONFLICT;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
