package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.Status.Code;

/**
 * Advice on retry for {@link Status}.
 */
public enum RetryAdvice {

    /**
     * For {@link Status} with code {@link Code#UNAVAILABLE}, the client can retry just the failing
     * call with exponential backoff. The minimum delay should be 1s unless it is documented
     * otherwise.
     */
    JUST_RETRY_FAILING_CALL,

    /**
     * For {@link Status} with code {@link Code#ABORTED}, the client should retry at a higher level
     * (e.g., when a client-specified test-and-set fails, indicating the client should restart a
     * read-modify-write sequence).
     *
     * <p>For {@link Status} with code {@link Code#RESOURCE_EXHAUSTED}, the client may retry at the
     * higher level with a delay determined by a sophisticated method.
     */
    RETRY_AT_HIGHER_LEVEL,

    /**
     * For {@link Status} with code {@link Code#FAILED_PRECONDITION}, the client should not retry
     * until the system state has been explicitly fixed. E.g., if an "rmdir" fails because the
     * directory is non-empty, `FAILED_PRECONDITION` should be returned since the client should not
     * retry unless the files are deleted from the directory.
     */
    NOT_RETRY_UNTIL_STATE_FIXED,

    /**
     * For all other status, retry may not be applicable - first ensure your request is idempotent.
     */
    NO_ADVICE
}
