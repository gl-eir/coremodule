package com.andHalf.smartHome.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class ServiceParameterException extends RuntimeException {
    public ServiceParameterException(String message) {
        super(message);
    }
}
