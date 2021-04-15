package ru.mos.devutils.kafka.controller;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.mos.devutils.kafka.client.KafkaClientExceptionWrapper;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(HttpServletRequest req, AuthenticationException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationException.class)
    public String handleAuthorizationException(HttpServletRequest req, AuthorizationException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(KafkaException.class)
    public String handleKafkaException(HttpServletRequest req, KafkaException exception) {
        return exception.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(KafkaClientExceptionWrapper.class)
    public String handleKafkaClientExceptionWrapper(HttpServletRequest req, KafkaClientExceptionWrapper wrapper) {
        return wrapper.getCause().getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleUnknownException(HttpServletRequest req, Exception exception) {
        return exception.getMessage();
    }
}
