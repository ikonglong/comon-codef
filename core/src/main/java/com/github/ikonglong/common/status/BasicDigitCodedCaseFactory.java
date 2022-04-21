package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.CodingStrategy;
import com.github.ikonglong.common.status.DigitCodedCase.Factory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A user can extend this class to define application-specific error cases.
 */
public abstract class BasicDigitCodedCaseFactory {

    protected final Factory caseFactory;

    protected BasicDigitCodedCaseFactory(Factory caseFactory) {
        this.caseFactory = caseFactory;
    }

    /**
     * Not an error; returned on success.
     *
     * <p>HTTP Mapping: 200 OK
     */
    public final Case newOk(int conditionCode) {
        return caseFactory.create(Status.Code.OK, conditionCode);
    }

    /**
     * The client specified an invalid argument. Note that this differs from `FAILED_PRECONDITION`.
     * `INVALID_ARGUMENT` indicates arguments that are problematic regardless of the state of the
     * system (e.g., a malformed file name).
     *
     * <p>HTTP Mapping: 400 Bad Request
     */
    public final Case newInvalidArgument(int conditionCode) {
        return caseFactory.create(Status.Code.INVALID_ARGUMENT, conditionCode);
    }

    public final Case newInvalidArgumentWithOffset(int offset) {
        return newCase(Status.Code.INVALID_ARGUMENT, offset);
    }

    /**
     * The operation was rejected because the system is not in a state required for the operation's
     * execution. For example, the directory to be deleted is non-empty, an rmdir operation is applied
     * to a non-directory, etc.
     *
     * <p>Service implementors can use the following guidelines to decide between
     * `FAILED_PRECONDITION`, `ABORTED`, and `UNAVAILABLE`: (a) Use `UNAVAILABLE` if the client can
     * retry just the failing call. (b) Use `ABORTED` if the client should retry at a higher level
     * (e.g., when a client-specified test-and-set fails, indicating the client should restart a
     * read-modify-write sequence). (c) Use `FAILED_PRECONDITION` if the client should not retry until
     * the system state has been explicitly fixed. E.g., if an "rmdir" fails because the directory is
     * non-empty, `FAILED_PRECONDITION` should be returned since the client should not retry unless
     * the files are deleted from the directory.
     *
     * <p>HTTP Mapping: 400 Bad Request
     */
    public final Case newFailedPrecondition(int conditionCode) {
        return caseFactory.create(Status.Code.FAILED_PRECONDITION, conditionCode);
    }

    public final Case newFailedPreconditionWithOffset(int offset) {
        return newCase(Status.Code.FAILED_PRECONDITION, offset);
    }

    /**
     * The operation was attempted past the valid range. E.g., seeking or reading past end-of-file.
     *
     * <p>Unlike `INVALID_ARGUMENT`, this error indicates a problem that may be fixed if the system
     * state changes. For example, a 32-bit file system will generate `INVALID_ARGUMENT` if asked to
     * read at an offset that is not in the range [0,2^32-1], but it will generate `OUT_OF_RANGE` if
     * asked to read from an offset past the current file size.
     *
     * <p>There is a fair bit of overlap between `FAILED_PRECONDITION` and `OUT_OF_RANGE`. We
     * recommend using `OUT_OF_RANGE` (the more specific error) when it applies so that callers who
     * are iterating through a space can easily look for an `OUT_OF_RANGE` error to detect when they
     * are done.
     *
     * <p>HTTP Mapping: 400 Bad Request
     */
    public final Case newOutOfRange(int conditionCode) {
        return caseFactory.create(Status.Code.OUT_OF_RANGE, conditionCode);
    }

    public final Case newOutOfRangeWithOffset(int offset) {
        return newCase(Status.Code.OUT_OF_RANGE, offset);
    }

    /**
     * The request does not have valid authentication credentials for the operation.
     *
     * <p>HTTP Mapping: 401 Unauthorized
     */
    public final Case newUnauthenticated(int conditionCode) {
        return caseFactory.create(Status.Code.UNAUTHENTICATED, conditionCode);
    }

    public final Case newUnauthenticatedWithOffset(int offset) {
        return newCase(Status.Code.UNAUTHENTICATED, offset);
    }

    /**
     * The caller does not have permission to execute the specified operation. `PERMISSION_DENIED`
     * must not be used for rejections caused by exhausting some resource (use `RESOURCE_EXHAUSTED`
     * instead for those errors). `PERMISSION_DENIED` must not be used if the caller can not be
     * identified (use `UNAUTHENTICATED` instead for those errors). This error code does not imply the
     * request is valid or the requested entity exists or satisfies other pre-conditions.
     *
     * <p>HTTP Mapping: 403 Forbidden
     */
    public final Case newPermissionDenied(int conditionCode) {
        return caseFactory.create(Status.Code.PERMISSION_DENIED, conditionCode);
    }

    public final Case newPermissionDeniedWithOffset(int offset) {
        return newCase(Status.Code.PERMISSION_DENIED, offset);
    }

    /**
     * Some requested entity (e.g., file or directory) was not found.
     *
     * <p>Note to server developers: if a request is denied for an entire class of users, such as
     * gradual feature rollout or undocumented whitelist, `NOT_FOUND` may be used. If a request is
     * denied for some users within a class of users, such as user-based access control,
     * `PERMISSION_DENIED` must be used.
     *
     * <p>HTTP Mapping: 404 Not Found
     */
    public final Case newNotFound(int conditionCode) {
        return caseFactory.create(Status.Code.NOT_FOUND, conditionCode);
    }

    public final Case newNotFoundWithOffset(int offset) {
        return newCase(Status.Code.NOT_FOUND, offset);
    }

    /**
     * The operation was aborted, typically due to a concurrency issue such as a sequencer check
     * failure or transaction abort.
     *
     * <p>See the guidelines above for deciding between `FAILED_PRECONDITION`, `ABORTED`, and
     * `UNAVAILABLE`.
     *
     * <p>HTTP Mapping: 409 Conflict ABORTED(409_220),
     *
     * <p>The entity that a client attempted to create (e.g., file or directory) already exists.
     *
     * <p>HTTP Mapping: 409 Conflict
     */
    public final Case newAlreadyExists(int conditionCode) {
        return caseFactory.create(Status.Code.ALREADY_EXISTS, conditionCode);
    }

    public final Case newAlreadyExistsWithOffset(int offset) {
        return newCase(Status.Code.ALREADY_EXISTS, offset);
    }

    /**
     * The operation was aborted, typically due to a concurrency issue such as a sequencer check
     * failure or transaction abort.
     *
     * <p>See the guidelines above for deciding between `FAILED_PRECONDITION`, `ABORTED`, and
     * `UNAVAILABLE`.
     *
     * <p>HTTP Mapping: 409 Conflict
     */
    public final Case newAborted(int conditionCode) {
        return caseFactory.create(Status.Code.ABORTED, conditionCode);
    }

    public final Case newAbortedWithOffset(int offset) {
        return newCase(Status.Code.ABORTED, offset);
    }

    /**
     * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
     * is out of space.
     *
     * <p>HTTP Mapping: 429 Too Many Requests
     */
    public final Case newResourceExhausted(int conditionCode) {
        return caseFactory.create(Status.Code.RESOURCE_EXHAUSTED, conditionCode);
    }

    public final Case newResourceExhaustedWithOffset(int offset) {
        return newCase(Status.Code.RESOURCE_EXHAUSTED, offset);
    }

    /**
     * The operation was cancelled, typically by the caller.
     *
     * <p>HTTP Mapping: 499 Client Closed Request
     */
    public final Case newCancelled(int conditionCode) {
        return caseFactory.create(Status.Code.CANCELLED, conditionCode);
    }

    public final Case newCancelledWithOffset(int offset) {
        return newCase(Status.Code.CANCELLED, offset);
    }

    /**
     * Internal errors. This means that some invariants expected by the underlying system have been
     * broken. This error code is reserved for serious errors.
     *
     * <p>HTTP Mapping: 500 Internal Server Error INTERNAL(500_280),
     *
     * <p>Unrecoverable data loss or corruption.
     *
     * <p>HTTP Mapping: 500 Internal Server Error
     */
    public final Case newDataLoss(int conditionCode) {
        return caseFactory.create(Status.Code.DATA_LOSS, conditionCode);
    }

    public final Case newDataLossWithOffset(int offset) {
        return newCase(Status.Code.DATA_LOSS, offset);
    }

    /**
     * Unknown error. For example, this error may be returned when a `Status` value received from
     * another address space belongs to an error space that is not known in this address space. Also
     * errors raised by APIs that do not return enough error information may be converted to this
     * error.
     *
     * <p>HTTP Mapping: 500 Internal Server Error
     */
    public final Case newUnknownError(int conditionCode) {
        return caseFactory.create(Status.Code.UNKNOWN, conditionCode);
    }

    public final Case newUnknownErrorWithOffset(int offset) {
        return newCase(Status.Code.UNKNOWN, offset);
    }

    /**
     * Internal errors. This means that some invariants expected by the underlying system have been
     * broken. This error code is reserved for serious errors.
     *
     * <p>HTTP Mapping: 500 Internal Server Error
     */
    public final Case newInternalError(int conditionCode) {
        return caseFactory.create(Status.Code.INTERNAL, conditionCode);
    }

    public final Case newInternalErrorWithOffset(int offset) {
        return newCase(Status.Code.INTERNAL, offset);
    }

    /**
     * The operation is not implemented or is not supported/enabled in this service.
     *
     * <p>HTTP Mapping: 501 Not Implemented
     */
    public final Case newUnimplemented(int conditionCode) {
        return caseFactory.create(Status.Code.UNIMPLEMENTED, conditionCode);
    }

    public final Case newUnimplementedWithOffset(int offset) {
        return newCase(Status.Code.UNIMPLEMENTED, offset);
    }

    /**
     * The service is currently unavailable. This is most likely a transient condition, which can be
     * corrected by retrying with a backoff.
     *
     * <p>See the guidelines above for deciding between `FAILED_PRECONDITION`, `ABORTED`, and
     * `UNAVAILABLE`.
     *
     * <p>HTTP Mapping: 503 Service Unavailable
     */
    public final Case newUnavailable(int conditionCode) {
        return caseFactory.create(Status.Code.UNAVAILABLE, conditionCode);
    }

    public final Case newUnavailableWithOffset(int offset) {
        return newCase(Status.Code.UNAVAILABLE, offset);
    }

    /**
     * The deadline expired before the operation could complete. For operations that change the state
     * of the system, this error may be returned even if the operation has completed successfully. For
     * example, a successful response from a server could have been delayed long enough for the
     * deadline to expire.
     *
     * <p>HTTP Mapping: 504 Gateway Timeout
     */
    public final Case newDeadLineExceeded(int conditionCode) {
        return caseFactory.create(Status.Code.DEADLINE_EXCEEDED, conditionCode);
    }

    public final Case newDeadLineExceededWithOffset(int offset) {
        return newCase(Status.Code.DEADLINE_EXCEEDED, offset);
    }

    public Case firstOk() {
        return newOk(startConditionCodeFor(Status.Code.OK));
    }

    public Case firstInvalidArgument() {
        return newInvalidArgument(startConditionCodeFor(Status.Code.INVALID_ARGUMENT));
    }

    public Case firstFailedPrecondition() {
        return newFailedPrecondition(startConditionCodeFor(Status.Code.FAILED_PRECONDITION));
    }

    public Case firstOutOfRange() {
        return newOutOfRange(startConditionCodeFor(Status.Code.OUT_OF_RANGE));
    }

    public Case firstUnauthenticated() {
        return newUnauthenticated(startConditionCodeFor(Status.Code.UNAUTHENTICATED));
    }

    public Case firstPermissionDenied() {
        return newPermissionDenied(startConditionCodeFor(Status.Code.PERMISSION_DENIED));
    }

    public Case firstNotFound() {
        return newNotFound(startConditionCodeFor(Status.Code.NOT_FOUND));
    }

    public Case firstAlreadyExists() {
        return newAlreadyExists(startConditionCodeFor(Status.Code.ALREADY_EXISTS));
    }

    public Case firstResourceExhausted() {
        return newResourceExhausted(startConditionCodeFor(Status.Code.RESOURCE_EXHAUSTED));
    }

    public Case firstAborted() {
        return newAborted(startConditionCodeFor(Status.Code.ABORTED));
    }

    public Case firstCancelled() {
        return newCancelled(startConditionCodeFor(Status.Code.CANCELLED));
    }

    public Case firstDataLoss() {
        return newDataLoss(startConditionCodeFor(Status.Code.DATA_LOSS));
    }

    public Case firstUnknownError() {
        return newUnknownError(startConditionCodeFor(Status.Code.UNKNOWN));
    }

    public Case firstInternalError() {
        return newInternalError(startConditionCodeFor(Status.Code.INTERNAL));
    }

    public Case firstUnimplemented() {
        return newUnimplemented(startConditionCodeFor(Status.Code.UNIMPLEMENTED));
    }

    public Case firstUnavailable() {
        return newUnavailable(startConditionCodeFor(Status.Code.UNAVAILABLE));
    }

    public Case firstDeadLineExceeded() {
        return newDeadLineExceeded(startConditionCodeFor(Status.Code.DEADLINE_EXCEEDED));
    }

    @Override
    public String toString() {
        String factoryStr = caseFactory.toString();
        String mapperStr = caseFactory.codingStrategy().statusCodeMapper().toString();
        return new StringBuilder(factoryStr.length() + mapperStr.length() + 20)
                .append(factoryStr)
                .append("\n\n")
                .append(mapperStr)
                .toString();
    }

    private final Case newCase(Status.Code statusCode, int offset) {
        checkArgument(offset > 0, "offset <= 0");
        return caseFactory.create(statusCode, startConditionCodeFor(statusCode) + offset);
    }

    private final int startConditionCodeFor(Status.Code statusCode) {
        return caseFactory
                .codingStrategy()
                .statusCodeMapper()
                .conditionCodeSegmentFor(statusCode)
                .start();
    }

    /**
     * This code factory is for a monolithic application.
     */
    public static class FactoryForMonolithicApp extends BasicDigitCodedCaseFactory {

        public FactoryForMonolithicApp() {
            super(
                    new Factory.Builder()
                            .codingStrategy(
                                    CodingStrategy.newBuilder()
                                            .numDigitsOfAppCode(0)
                                            .numDigitsOfModuleCode(0)
                                            .numDigitsOfConditionCode(3)
                                            .statusCodeMapper(new StatusCodeToNumRangeMapper())
                                            .build())
                            .build());
        }
    }

    /**
     * This code factory is for an application which consists of multiple(<10) modules.
     */
    public static class FactoryForMultiModuleApp extends BasicDigitCodedCaseFactory {

        public FactoryForMultiModuleApp(int moduleCode) {
            super(
                    new Factory.Builder()
                            .codingStrategy(
                                    CodingStrategy.newBuilder()
                                            .numDigitsOfAppCode(0)
                                            .numDigitsOfModuleCode(1)
                                            .numDigitsOfConditionCode(3)
                                            .statusCodeMapper(new StatusCodeToNumRangeMapper())
                                            .build())
                            .moduleCode(moduleCode)
                            .build());
        }
    }

    /**
     * This code factory is for a system which consists of multiple(<10) applications, and some or all
     * of the apps consist of multiple(<10) modules.
     */
    public static class FactoryForComplexSystem extends BasicDigitCodedCaseFactory {

        public FactoryForComplexSystem(int appCode, int moduleCode) {
            super(
                    new Factory.Builder()
                            .codingStrategy(
                                    CodingStrategy.newBuilder()
                                            .numDigitsOfAppCode(1)
                                            .numDigitsOfModuleCode(1)
                                            .numDigitsOfConditionCode(3)
                                            .statusCodeMapper(new StatusCodeToNumRangeMapper())
                                            .build())
                            .appCode(appCode)
                            .moduleCode(moduleCode)
                            .build());
        }
    }
}
