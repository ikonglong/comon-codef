package com.github.ikonglong.common.status;

import static java.lang.String.format;

public enum HttpStatus {

    /**
     * 200 OK
     */
    OK(200),

    /**
     * 400 Bad Request
     */
    BAD_REQUEST(400),

    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED(401),

    /**
     * 403 Forbidden
     */
    FORBIDDEN(403),

    /**
     * 404 Not Found
     */
    NOT_FOUND(404),

    /**
     * 409 Conflict
     */
    CONFLICT(409),

    /**
     * 429 Too Many Requests
     */
    TOO_MANY_REQUESTS(429),

    /**
     * 499 Client Closed Request
     */
    CLIENT_CLOSED_REQUEST(499),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(500),

    /**
     * 501 Not Implemented
     */
    NOT_IMPLEMENTED(501),

    /**
     * 503 Service Unavailable
     */
    SERVICE_UNAVAILABLE(503),

    /**
     * 504 Gateway Timeout
     */
    TIMEOUT(504);

    private int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public static boolean isDefined(int statusCode) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.code == statusCode) {
                return true;
            }
        }
        return false;
    }

    public static HttpStatus fromCode(int statusCode) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.code == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                format("Http status for code %d is not defined.", statusCode));
    }

    public int code() {
        return code;
    }

    @Override
    public String toString() {
        return this.name() + ":" + code();
    }
}
