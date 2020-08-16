package com.sample.project.analytics.service;

/**
 * @author arvin on 2/15/20
 **/
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
