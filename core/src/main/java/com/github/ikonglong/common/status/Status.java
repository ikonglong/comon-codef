package com.github.ikonglong.common.status;

import com.google.common.base.MoreObjects;
import com.github.ikonglong.common.status.guava.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.*;

import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Defines the status of an operation by providing a standard {@link Code} in conjunction with an
 * optional descriptive message. Instances of {@code Status} are created by starting with the
 * template for the appropriate {@link Status.Code} and supplementing it with additional
 * information: {@code Status.NOT_FOUND.withMessage("Could not find 'important_file.txt'");}
 *
 * <p>For choosing the appropriate template, please see the docs for {@link Code}.
 *
 * <p>For clients, every remote call will return a status on completion. In the case of errors this
 * status may be propagated to blocking stubs as a {@link RuntimeException} or to a listener as an
 * explicit parameter.
 *
 * <p>Similarly servers can report a status by throwing {@link StatusRuntimeException} or by passing
 * the status to a callback.
 *
 * <p>Utility functions are provided to convert a status to an exception and to extract them back
 * out.
 */
@Immutable
public class Status {

    private static final Map<Code, HttpStatus> STATUS_CODE_TO_HTTP_STATUS = buildCodeMappings();
    // Create the canonical list of Status instances indexed by their code values.
    private static final List<Status> STATUS_LIST = buildStatusList();
    /**
     * The operation completed successfully.
     */
    public static final Status OK = Code.OK.toStatus();
    /**
     * The operation was cancelled (typically by the caller).
     */
    public static final Status CANCELLED = Code.CANCELLED.toStatus();
    /**
     * Unknown error. See {@link Code#UNKNOWN}.
     */
    public static final Status UNKNOWN = Code.UNKNOWN.toStatus();
    /**
     * Client specified an invalid argument. See {@link Code#INVALID_ARGUMENT}.
     */
    public static final Status INVALID_ARGUMENT = Code.INVALID_ARGUMENT.toStatus();

    // A pseudo-enum of Status instances mapped 1:1 with values in Code. This simplifies construction
    // patterns for derived instances of Status.
    /**
     * Deadline expired before operation could complete. See {@link Code#DEADLINE_EXCEEDED}.
     */
    public static final Status DEADLINE_EXCEEDED = Code.DEADLINE_EXCEEDED.toStatus();
    /**
     * Some requested entity (e.g., file or directory) was not found.
     */
    public static final Status NOT_FOUND = Code.NOT_FOUND.toStatus();
    /**
     * Some entity that we attempted to create (e.g., file or directory) already exists.
     */
    public static final Status ALREADY_EXISTS = Code.ALREADY_EXISTS.toStatus();
    /**
     * The caller does not have permission to execute the specified operation. See {@link
     * Code#PERMISSION_DENIED}.
     */
    public static final Status PERMISSION_DENIED = Code.PERMISSION_DENIED.toStatus();
    /**
     * The request does not have valid authentication credentials for the operation.
     */
    public static final Status UNAUTHENTICATED = Code.UNAUTHENTICATED.toStatus();
    /**
     * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
     * is out of space.
     */
    public static final Status RESOURCE_EXHAUSTED = Code.RESOURCE_EXHAUSTED.toStatus();
    /**
     * Operation was rejected because the system is not in a state required for the operation's
     * execution. See {@link Code#FAILED_PRECONDITION}.
     */
    public static final Status FAILED_PRECONDITION = Code.FAILED_PRECONDITION.toStatus();
    /**
     * The operation was aborted, typically due to a concurrency issue like sequencer check failures,
     * transaction aborts, etc. See {@link Code#ABORTED}.
     */
    public static final Status ABORTED = Code.ABORTED.toStatus();
    /**
     * Operation was attempted past the valid range. See {@link Code#OUT_OF_RANGE}.
     */
    public static final Status OUT_OF_RANGE = Code.OUT_OF_RANGE.toStatus();
    /**
     * Operation is not implemented or not supported/enabled in this service.
     */
    public static final Status UNIMPLEMENTED = Code.UNIMPLEMENTED.toStatus();
    /**
     * Internal errors. See {@link Code#INTERNAL}.
     */
    public static final Status INTERNAL = Code.INTERNAL.toStatus();
    /**
     * The service is currently unavailable. See {@link Code#UNAVAILABLE}.
     */
    public static final Status UNAVAILABLE = Code.UNAVAILABLE.toStatus();
    private static final Map<HttpStatus, Status> HTTP_CODE_TO_DEFAULT_STATUS =
            buildHttpStatusMapping();
    /**
     * Unrecoverable data loss or corruption.
     */
    public static final Status DATA_LOSS = Code.DATA_LOSS.toStatus();

    private Code code;
    private Case theCase;
    private String message;
    private List<Object> details;

    private Status(Code code) {
        this(code, null, null, null);
    }

    private Status(
            Code code, @Nullable String message, @Nullable Case theCase, @Nullable List<Object> details) {
        this.code = requireNonNull(code, "code");
        this.message = message;
        this.theCase = theCase;
        this.details = isNull(details) ? Collections.emptyList() : details;
    }

    private static Map<Code, HttpStatus> buildCodeMappings() {
        Map<Code, HttpStatus> mappings = new HashMap<>(Code.values().length);
        mappings.put(Code.OK, HttpStatus.OK);
        mappings.put(Code.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST);
        mappings.put(Code.FAILED_PRECONDITION, HttpStatus.BAD_REQUEST);
        mappings.put(Code.OUT_OF_RANGE, HttpStatus.BAD_REQUEST);
        mappings.put(Code.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED);
        mappings.put(Code.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        mappings.put(Code.NOT_FOUND, HttpStatus.NOT_FOUND);
        mappings.put(Code.ABORTED, HttpStatus.CONFLICT);
        mappings.put(Code.ALREADY_EXISTS, HttpStatus.CONFLICT);
        mappings.put(Code.RESOURCE_EXHAUSTED, HttpStatus.TOO_MANY_REQUESTS);
        mappings.put(Code.CANCELLED, HttpStatus.CLIENT_CLOSED_REQUEST);
        mappings.put(Code.DATA_LOSS, HttpStatus.INTERNAL_SERVER_ERROR);
        mappings.put(Code.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR);
        mappings.put(Code.INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR);
        mappings.put(Code.UNIMPLEMENTED, HttpStatus.NOT_IMPLEMENTED);
        mappings.put(Code.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        mappings.put(Code.DEADLINE_EXCEEDED, HttpStatus.TIMEOUT);
        return Collections.unmodifiableMap(mappings);
    }

    private static Map<HttpStatus, Status> buildHttpStatusMapping() {
        Map<HttpStatus, Status> mappings = new HashMap<>(HttpStatus.values().length);
        mappings.put(HttpStatus.OK, Status.OK);
        mappings.put(HttpStatus.BAD_REQUEST, Status.INVALID_ARGUMENT);
        mappings.put(HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED);
        mappings.put(HttpStatus.FORBIDDEN, Status.PERMISSION_DENIED);
        mappings.put(HttpStatus.NOT_FOUND, Status.NOT_FOUND);
        mappings.put(HttpStatus.CONFLICT, Status.ALREADY_EXISTS);
        mappings.put(HttpStatus.TOO_MANY_REQUESTS, Status.RESOURCE_EXHAUSTED);
        mappings.put(HttpStatus.CLIENT_CLOSED_REQUEST, Status.CANCELLED);
        mappings.put(HttpStatus.INTERNAL_SERVER_ERROR, Status.INTERNAL);
        mappings.put(HttpStatus.NOT_IMPLEMENTED, Status.UNIMPLEMENTED);
        mappings.put(HttpStatus.SERVICE_UNAVAILABLE, Status.UNAVAILABLE);
        mappings.put(HttpStatus.TIMEOUT, Status.DEADLINE_EXCEEDED);
        return Collections.unmodifiableMap(mappings);
    }

    private static List<Status> buildStatusList() {
        TreeMap<Integer, Status> canonicalizer = new TreeMap<Integer, Status>();
        for (Code code : Code.values()) {
            Status replaced = canonicalizer.put(code.value(), new Status(code));
            if (replaced != null) {
                throw new IllegalStateException(
                        "Code value duplication between " + replaced.code().name() + " & " + code.name());
            }
        }
        return Collections.unmodifiableList(new ArrayList<Status>(canonicalizer.values()));
    }

    public static Status fromHttpStatusCode(int statusCode) {
        if (HttpStatus.isDefined(statusCode)) {
            return requireNonNull(
                    HTTP_CODE_TO_DEFAULT_STATUS.get(HttpStatus.fromCode(statusCode)),
                    () -> format("http status for code %d", statusCode));
        }
        return Status.UNKNOWN;
    }

    /**
     * Return a {@link Status} given a canonical error {@link Code} value.
     */
    public static Status fromCodeValue(int codeValue) {
        if (codeValue < 0 || codeValue > STATUS_LIST.size()) {
            return UNKNOWN.withMessage("Unknown code " + codeValue);
        } else {
            return STATUS_LIST.get(codeValue);
        }
    }

    /**
     * Return a {@link Status} given a canonical error {@link Code} value.
     */
    public static Status fromCode(Code code) {
        return code.toStatus();
    }

    /**
     * Return a {@link Status} given a {@link Case} and a descriptive message.
     *
     * <p>Attention! The `msgTemplate` arg is a string that takes only {@code "%s"} as placeholder.
     * Other char sequences with prefix '%' are not treated as placeholders. If the placeholder and
     * argument counts do not match, format operation returns a best-effort form of that string. Will
     * not throw an exception under normal conditions.
     *
     * @param msgTemplate a string containing zero or more {@code "%s"} placeholder sequences. {@code
     *                    null} is treated as the four-character string {@code "null"}.
     * @param msgArgs     the arguments to be substituted into the message template. A {@code null}
     *                    argument is converted to the four-character string {@code "null"}; non-null values are
     *                    converted to strings using {@link Object#toString()}.
     */
    public static Status fromCase(Case theCase, String msgTemplate, Object... msgArgs) {
        return theCase.statusCode().toStatus().withCase(theCase, msgTemplate, msgArgs);
    }

    /**
     * Extract an error {@link Status} from the causal chain of a {@link Throwable}. If no status can
     * be found, a status is created with {@link Code#UNKNOWN} as its code and {@code t} as its cause.
     *
     * @return non-{@code null} status
     */
    public static Status fromThrowable(Throwable t) {
        Throwable cause = checkNotNull(t, "t");
        while (cause != null) {
            if (cause instanceof StatusException) {
                return ((StatusException) cause).getStatus();
            } else if (cause instanceof StatusRuntimeException) {
                return ((StatusRuntimeException) cause).getStatus();
            }
            cause = cause.getCause();
        }
        // Couldn't find a cause with a Status
        return UNKNOWN;
    }

    static String formatThrowableMessage(Status status) {
        if (status.message == null) {
            return status.code.toString();
        } else {
            return status.code + ": " + status.message;
        }
    }

    private static Status fromCodeValue(byte[] asciiCodeValue) {
        if (asciiCodeValue.length == 1 && asciiCodeValue[0] == '0') {
            return Status.OK;
        }
        return fromCodeValueSlow(asciiCodeValue);
    }

    @SuppressWarnings("fallthrough")
    private static Status fromCodeValueSlow(byte[] asciiCodeValue) {
        int index = 0;
        int codeValue = 0;
        switch (asciiCodeValue.length) {
            case 2:
                if (asciiCodeValue[index] < '0' || asciiCodeValue[index] > '9') {
                    break;
                }
                codeValue += (asciiCodeValue[index++] - '0') * 10;
                // fall through
            case 1:
                if (asciiCodeValue[index] < '0' || asciiCodeValue[index] > '9') {
                    break;
                }
                codeValue += asciiCodeValue[index] - '0';
                if (codeValue < STATUS_LIST.size()) {
                    return STATUS_LIST.get(codeValue);
                }
                break;
            default:
                break;
        }
        return UNKNOWN.withMessage("Unknown code " + new String(asciiCodeValue, US_ASCII));
    }

    private boolean isDetailsSet() {
        return details != Collections.emptyList();
    }

    /**
     * Create a derived instance of {@link Status} with the given message. Leading and trailing
     * whitespace may be removed; this may change in the future.
     */
    public Status withMessage(String message) {
        if (Objects.equals(this.message, message)) {
            return this;
        }
        return new Status(this.code, message, this.theCase, this.details);
    }

    /**
     * Create a derived instance of {@link Status} with the given message.
     *
     * <p>Attention! The `msgTemplate` arg is a string that takes only {@code "%s"} as placeholder.
     * Other char sequences with prefix '%' are not treated as placeholders. If the placeholder and
     * argument counts do not match, format operation returns a best-effort form of that string. Will
     * not throw an exception under normal conditions.
     *
     * @param msgTemplate a string containing zero or more {@code "%s"} placeholder sequences. {@code
     *                    null} is treated as the four-character string {@code "null"}.
     * @param msgArgs     the arguments to be substituted into the message template. A {@code null}
     *                    argument is converted to the four-character string {@code "null"}; non-null values are
     *                    converted to strings using {@link Object#toString()}.
     */
    public Status withMessage(String msgTemplate, Object... msgArgs) {
        return withMessage(Strings.lenientFormat(msgTemplate, msgArgs));
    }

    /**
     * Create a derived instance of {@link Status} augmenting the current message with additional
     * detail. Leading and trailing whitespace may be removed; this may change in the future.
     */
    public Status augmentMessage(String additionalDetail) {
        if (additionalDetail == null) {
            return this;
        } else if (this.message == null) {
            return new Status(this.code, additionalDetail, this.theCase, this.details);
        } else {
            return new Status(
                    this.code, this.message + "\n" + additionalDetail, this.theCase, this.details);
        }
    }

    /**
     * Create a derived instance of {@link Status} with the given {@link Case}.
     */
    public Status withCase(Case theCase) {
        if (Objects.equals(this.theCase, theCase)) {
            return this;
        }
        return new Status(this.code, this.message, theCase, this.details);
    }

    /**
     * Create a derived instance of {@link Status} with the given {@link Case} and message.
     *
     * <p>Attention! The `msgTemplate` arg is a string that takes only {@code "%s"} as placeholder.
     * Other char sequences with prefix '%' are not treated as placeholders. If the placeholder and
     * argument counts do not match, format operation returns a best-effort form of that string. Will
     * not throw an exception under normal conditions.
     *
     * @param msgTemplate a string containing zero or more {@code "%s"} placeholder sequences. {@code
     *                    null} is treated as the four-character string {@code "null"}.
     * @param msgArgs     the arguments to be substituted into the message template. A {@code null}
     *                    argument is converted to the four-character string {@code "null"}; non-null values are
     *                    converted to strings using {@link Object#toString()}.
     */
    public Status withCase(Case theCase, String msgTemplate, Object... msgArgs) {
        String message = Strings.lenientFormat(msgTemplate, msgArgs);
        if (Objects.equals(this.message, message) || Objects.equals(this.theCase, theCase)) {
            return this;
        }
        return new Status(this.code, message, theCase, this.details);
    }

    /**
     * Add a detail about the failure.
     */
    public Status addDetail(Object detail) {
        requireNonNull(detail, "detail");
        if (!isDetailsSet()) {
            details = new ArrayList<>(3);
        }
        details.add(detail);
        return this;
    }

    public Status addDetails(Iterable details) {
        requireNonNull(details, "details");
        if (!isDetailsSet()) {
            this.details = new ArrayList<>(3);
        }
        for (Object e : details) {
            this.details.add(e);
        }
        return this;
    }

    /**
     * The canonical status code.
     */
    public Code code() {
        return code;
    }

    /**
     * A message of this status for human consumption.
     */
    @Nullable
    public String message() {
        return message;
    }

    /**
     * A message of this status for human consumption of which the template is "Status: {Status Name},
     * reasonPhrase: {reasonPhrase}".
     */
    @Nonnull
    public String messagePrefixedWithStatus() {
        return new StringBuilder(message.length() + 30)
                .append("Status: ")
                .append(code.name())
                .append(", reasonPhrase: ")
                .append(message)
                .toString();
    }

    @Nullable
    public Case theCase() {
        return theCase;
    }

    public List<?> details() {
        return details;
    }

    /**
     * Provides advice on retry for this status.
     */
    @Nonnull
    public RetryAdvice retryAdvice() {
        RetryAdvice advice;
        if (this.code == Code.UNAVAILABLE) {
            advice = RetryAdvice.JUST_RETRY_FAILING_CALL;
        } else if (this.code == Code.FAILED_PRECONDITION) {
            advice = RetryAdvice.NOT_RETRY_UNTIL_STATE_FIXED;
        } else if (this.code == Code.ABORTED || this.code == Code.RESOURCE_EXHAUSTED) {
            advice = RetryAdvice.RETRY_AT_HIGHER_LEVEL;
        } else {
            advice = RetryAdvice.NO_ADVICE;
        }
        return advice;
    }

    /**
     * Is this status OK, i.e., not an error.
     */
    public boolean isOk() {
        return Code.OK == code;
    }

    /**
     * Convert this {@link Status} to a {@link RuntimeException}. Use {@link #fromThrowable} to
     * recover this {@link Status} instance when the returned exception is in the causal chain.
     */
    public StatusRuntimeException asRuntimeException() {
        return new StatusRuntimeException(this);
    }

    /**
     * Convert this {@link Status} to an {@link Exception}. Use {@link #fromThrowable} to recover this
     * {@link Status} instance when the returned exception is in the causal chain.
     */
    public StatusException asException() {
        return new StatusException(this);
    }

    /**
     * A string representation of the status useful for debugging.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code.value)
                .add("status", code.name())
                .add("message", message)
                .add("case", theCase)
                .add("details", Objects.toString(details))
                .toString();
    }

    /**
     * Equality on Statuses is not well defined. Instead, do comparison based on their Code with
     * {@link #code}. The description and cause of the Status are unlikely to be stable, and
     * additional fields may be added to Status in the future.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Hash codes on Statuses are not well defined.
     *
     * @see #equals
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * The set of canonical status codes.
     *
     * <p>Sometimes multiple error codes may apply. Services should return the most specific error
     * code that applies. For example, prefer `OUT_OF_RANGE` over `FAILED_PRECONDITION` if both codes
     * apply. Similarly prefer `NOT_FOUND` or `ALREADY_EXISTS` over `FAILED_PRECONDITION`.
     */
    public enum Code {
        /**
         * The operation completed successfully.
         */
        OK(0),

        /**
         * The operation was cancelled (typically by the caller).
         */
        CANCELLED(1),

        /**
         * Unknown error. An example of where this error may be returned is if a Status value received
         * from another address space belongs to an error-space that is not known in this address space.
         * Also errors raised by APIs that do not return enough error information may be converted to
         * this error.
         */
        UNKNOWN(2),

        /**
         * Client specified an invalid argument. Note that this differs from FAILED_PRECONDITION.
         * INVALID_ARGUMENT indicates arguments that are problematic regardless of the state of the
         * system (e.g., a malformed file name).
         */
        INVALID_ARGUMENT(3),

        /**
         * Deadline expired before operation could complete. For operations that change the state of the
         * system, this error may be returned even if the operation has completed successfully. For
         * example, a successful response from a server could have been delayed long enough for the
         * deadline to expire.
         */
        DEADLINE_EXCEEDED(4),

        /**
         * Some requested entity (e.g., file or directory) was not found.
         */
        NOT_FOUND(5),

        /**
         * Some entity that we attempted to create (e.g., file or directory) already exists.
         */
        ALREADY_EXISTS(6),

        /**
         * The caller does not have permission to execute the specified operation. PERMISSION_DENIED
         * must not be used for rejections caused by exhausting some resource (use RESOURCE_EXHAUSTED
         * instead for those errors). PERMISSION_DENIED must not be used if the caller cannot be
         * identified (use UNAUTHENTICATED instead for those errors).
         */
        PERMISSION_DENIED(7),

        /**
         * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
         * is out of space.
         */
        RESOURCE_EXHAUSTED(8),

        /**
         * Operation was rejected because the system is not in a state required for the operation's
         * execution. For example, directory to be deleted may be non-empty, an rmdir operation is
         * applied to a non-directory, etc.
         *
         * <p>A litmus test that may help a service implementor in deciding between FAILED_PRECONDITION,
         * ABORTED, and UNAVAILABLE: (a) Use UNAVAILABLE if the client can retry just the failing call.
         * (b) Use ABORTED if the client should retry at a higher-level (e.g., restarting a
         * read-modify-write sequence). (c) Use FAILED_PRECONDITION if the client should not retry until
         * the system state has been explicitly fixed. E.g., if an "rmdir" fails because the directory
         * is non-empty, FAILED_PRECONDITION should be returned since the client should not retry unless
         * they have first fixed up the directory by deleting files from it.
         */
        FAILED_PRECONDITION(9),

        /**
         * The operation was aborted, typically due to a concurrency issue like sequencer check
         * failures, transaction aborts, etc.
         *
         * <p>See litmus test above for deciding between FAILED_PRECONDITION, ABORTED, and UNAVAILABLE.
         */
        ABORTED(10),

        /**
         * Operation was attempted past the valid range. E.g., seeking or reading past end of file.
         *
         * <p>Unlike INVALID_ARGUMENT, this error indicates a problem that may be fixed if the system
         * state changes. For example, a 32-bit file system will generate INVALID_ARGUMENT if asked to
         * read at an offset that is not in the range [0,2^32-1], but it will generate OUT_OF_RANGE if
         * asked to read from an offset past the current file size.
         *
         * <p>There is a fair bit of overlap between FAILED_PRECONDITION and OUT_OF_RANGE. We recommend
         * using OUT_OF_RANGE (the more specific error) when it applies so that callers who are
         * iterating through a space can easily look for an OUT_OF_RANGE error to detect when they are
         * done.
         */
        OUT_OF_RANGE(11),

        /**
         * Operation is not implemented or not supported/enabled in this service.
         */
        UNIMPLEMENTED(12),

        /**
         * Internal errors. Means some invariants expected by underlying system has been broken. If you
         * see one of these errors, something is very broken.
         */
        INTERNAL(13),

        /**
         * The service is currently unavailable. This is a most likely a transient condition and may be
         * corrected by retrying with a backoff.
         *
         * <p>See litmus test above for deciding between FAILED_PRECONDITION, ABORTED, and UNAVAILABLE.
         */
        UNAVAILABLE(14),

        /**
         * Unrecoverable data loss or corruption.
         */
        DATA_LOSS(15),

        /**
         * The request does not have valid authentication credentials for the operation.
         */
        UNAUTHENTICATED(16);

        private final int value;

        @SuppressWarnings("ImmutableEnumChecker") // we make sure the byte[] can't be modified
        private final byte[] valueAscii;

        private Code(int value) {
            this.value = value;
            this.valueAscii = Integer.toString(value).getBytes(US_ASCII);
        }

        /**
         * The numerical value of the code.
         */
        public int value() {
            return value;
        }

        /**
         * Returns a {@link Status} object corresponding to this status code.
         */
        public Status toStatus() {
            return STATUS_LIST.get(value);
        }

        /**
         * Returns the http code corresponding to this status code.
         *
         * <p>Deprecated, please use {@link Code#toHttpStatus()} instead.
         */
        @Deprecated
        public int toHttpCode() {
            return toHttpStatus().code();
        }

        /**
         * Returns the http code enum corresponding to this status code.
         */
        public HttpStatus toHttpStatus() {
            return STATUS_CODE_TO_HTTP_STATUS.get(this);
        }

        private byte[] valueAscii() {
            return valueAscii;
        }
    }
}
