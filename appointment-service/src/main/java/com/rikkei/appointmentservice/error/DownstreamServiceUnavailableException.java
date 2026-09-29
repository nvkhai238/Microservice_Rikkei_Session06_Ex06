package com.rikkei.appointmentservice.error;

public class DownstreamServiceUnavailableException extends RuntimeException {

    public DownstreamServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

