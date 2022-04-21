package com.github.ikonglong.common.status;

public class StatusRuntimeException extends RuntimeException {

    private final Status status;

    public StatusRuntimeException(Status status) {
        super(Status.formatThrowableMessage(status));
        this.status = status;
    }

    public StatusRuntimeException(Status status, Throwable cause) {
        super(Status.formatThrowableMessage(status), cause);
        this.status = status;
    }

    public final Status getStatus() {
        return status;
    }
}
