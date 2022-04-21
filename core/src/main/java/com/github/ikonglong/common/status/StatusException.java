package com.github.ikonglong.common.status;

public class StatusException extends Exception {

    private final Status status;

    public StatusException(Status status) {
        super(Status.formatThrowableMessage(status));
        this.status = status;
    }

    public StatusException(Status status, Throwable cause) {
        super(Status.formatThrowableMessage(status), cause);
        this.status = status;
    }

    public final Status getStatus() {
        return status;
    }
}
