package com.github.ikonglong.common.status;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.github.ikonglong.common.status.DigitCodedCase.NumRange;
import com.github.ikonglong.common.status.Status.Code;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static java.util.Objects.nonNull;

/**
 * This mapper maps every {@link Status.Code} to a condition code segment which is represented by an
 * object of {@link NumRange}.
 */
public abstract class StatusCodeMapper {

    private BiMap<Status.Code, NumRange> statusCodeToConditionCodeSegment =
            HashBiMap.create(Code.values().length);

    protected StatusCodeMapper() {
        statusCodeToConditionCodeSegment.put(Status.Code.OK, ok());
        statusCodeToConditionCodeSegment.put(Status.Code.INVALID_ARGUMENT, invalidArgument());
        statusCodeToConditionCodeSegment.put(Status.Code.FAILED_PRECONDITION, failedPrecondition());
        statusCodeToConditionCodeSegment.put(Status.Code.OUT_OF_RANGE, outOfRange());
        statusCodeToConditionCodeSegment.put(Status.Code.UNAUTHENTICATED, unauthenticated());
        statusCodeToConditionCodeSegment.put(Status.Code.PERMISSION_DENIED, permissionDenied());
        statusCodeToConditionCodeSegment.put(Status.Code.NOT_FOUND, notFound());
        statusCodeToConditionCodeSegment.put(Status.Code.ALREADY_EXISTS, alreadyExists());
        statusCodeToConditionCodeSegment.put(Status.Code.ABORTED, aborted());
        statusCodeToConditionCodeSegment.put(Status.Code.RESOURCE_EXHAUSTED, resourceExhausted());
        statusCodeToConditionCodeSegment.put(Status.Code.CANCELLED, cancelled());
        statusCodeToConditionCodeSegment.put(Status.Code.DATA_LOSS, dataLoss());
        statusCodeToConditionCodeSegment.put(Status.Code.UNKNOWN, unknownError());
        statusCodeToConditionCodeSegment.put(Status.Code.INTERNAL, internalError());
        statusCodeToConditionCodeSegment.put(Status.Code.UNIMPLEMENTED, unimplemented());
        statusCodeToConditionCodeSegment.put(Status.Code.UNAVAILABLE, unavailable());
        statusCodeToConditionCodeSegment.put(Status.Code.DEADLINE_EXCEEDED, deadlineExceeded());
    }

    public boolean hasMappingFor(Status.Code statusCode) {
        return statusCodeToConditionCodeSegment.containsKey(statusCode);
    }

    public NumRange conditionCodeSegmentFor(Status.Code statusCode) {
        return statusCodeToConditionCodeSegment.get(statusCode);
    }

    public Collection<NumRange> conditionCodeSegments() {
        return statusCodeToConditionCodeSegment.values();
    }

    public Map<Status.Code, NumRange> mappings() {
        return Collections.unmodifiableMap(statusCodeToConditionCodeSegment);
    }

    public Map<NumRange, Status.Code> inverseMappings() {
        return Collections.unmodifiableMap(statusCodeToConditionCodeSegment.inverse());
    }

    public SortedMap<NumRange, Code> sortedInverseMappings() {
        return new TreeMap<>(statusCodeToConditionCodeSegment.inverse());
    }

    /**
     * Declare a mapping from {@link Status.Code#OK} to a condition code segment represented by the
     * return {@link NumRange}.
     */
    protected abstract NumRange ok();

    /**
     * Declare a mapping from {@link Status.Code#INVALID_ARGUMENT} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange invalidArgument();

    /**
     * Declare a mapping from {@link Status.Code#FAILED_PRECONDITION} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange failedPrecondition();

    /**
     * Declare a mapping from {@link Status.Code#OUT_OF_RANGE} to a condition code segment represented
     * by the return {@link NumRange}.
     */
    protected abstract NumRange outOfRange();

    /**
     * Declare a mapping from {@link Status.Code#UNAUTHENTICATED} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange unauthenticated();

    /**
     * Declare a mapping from {@link Status.Code#PERMISSION_DENIED} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange permissionDenied();

    /**
     * Declare a mapping from {@link Status.Code#NOT_FOUND} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange notFound();

    /**
     * Declare a mapping from {@link Status.Code#ALREADY_EXISTS} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange alreadyExists();

    /**
     * Declare a mapping from {@link Status.Code#ABORTED} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange aborted();

    /**
     * Declare a mapping from {@link Status.Code#RESOURCE_EXHAUSTED} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange resourceExhausted();

    /**
     * Declare a mapping from {@link Status.Code#CANCELLED} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange cancelled();

    /**
     * Declare a mapping from {@link Status.Code#DATA_LOSS} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange dataLoss();

    /**
     * Declare a mapping from {@link Status.Code#UNKNOWN} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange unknownError();

    /**
     * Declare a mapping from {@link Status.Code#INTERNAL} to a condition code segment represented by
     * the return {@link NumRange}.
     */
    protected abstract NumRange internalError();

    /**
     * Declare a mapping from {@link Status.Code#UNIMPLEMENTED} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange unimplemented();

    /**
     * Declare a mapping from {@link Status.Code#UNAVAILABLE} to a condition code segment represented
     * by the return {@link NumRange}.
     */
    protected abstract NumRange unavailable();

    /**
     * Declare a mapping from {@link Status.Code#DEADLINE_EXCEEDED} to a condition code segment
     * represented by the return {@link NumRange}.
     */
    protected abstract NumRange deadlineExceeded();

    @Override
    public String toString() {
        SortedMap<NumRange, Code> sortedMap = sortedInverseMappings();
        PrintStream output = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream((sortedMap.size() + 2) * 90 * 2);
            output = new PrintStream(byteStream);
            output.printf("Mapping between condition code and status:\n\n");
            output.printf(
                    "| %22s | %23s | %28s |\n",
                    "Condition Code Segment", "Status (Name:Code)", "HTTP Status (Name:Code)");
            String format = "| %22s | %20s:%2s | %24s:%3s |\n";
            for (Map.Entry<NumRange, Code> kv : sortedMap.entrySet()) {
                HttpStatus httpStatus = kv.getValue().toHttpStatus();
                output.printf(
                        format,
                        kv.getKey().toString(),
                        kv.getValue().name(),
                        kv.getValue().value(),
                        httpStatus.name(),
                        httpStatus.code());
            }
            output.flush();
            return byteStream.toString();
        } finally {
            if (nonNull(output)) output.close();
        }
    }
}
