package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.NumRange;

/**
 * This mapper maps all {@link Status.Code}s to consecutive condition code segments of which the
 * size are 50. The segments are produced by splitting the 3-digit condition code range [0-999]
 * every 50 numbers.
 */
public class StatusCodeToNumRangeMapper extends StatusCodeMapper {

    @Override
    protected NumRange ok() {
        return NumRange.of(0, 0);
    }

    @Override
    protected NumRange invalidArgument() {
        return NumRange.of(1, 50);
    }

    @Override
    protected NumRange failedPrecondition() {
        return NumRange.of(51, 100);
    }

    @Override
    protected NumRange outOfRange() {
        return NumRange.of(101, 150);
    }

    @Override
    protected NumRange unauthenticated() {
        return NumRange.of(151, 200);
    }

    @Override
    protected NumRange permissionDenied() {
        return NumRange.of(201, 250);
    }

    @Override
    protected NumRange notFound() {
        return NumRange.of(251, 300);
    }

    @Override
    protected NumRange alreadyExists() {
        return NumRange.of(301, 350);
    }

    @Override
    protected NumRange aborted() {
        return NumRange.of(351, 400);
    }

    @Override
    protected NumRange resourceExhausted() {
        return NumRange.of(401, 450);
    }

    @Override
    protected NumRange cancelled() {
        return NumRange.of(451, 500);
    }

    @Override
    protected NumRange dataLoss() {
        return NumRange.of(501, 550);
    }

    @Override
    protected NumRange unknownError() {
        return NumRange.of(551, 600);
    }

    @Override
    protected NumRange internalError() {
        return NumRange.of(601, 650);
    }

    @Override
    protected NumRange unimplemented() {
        return NumRange.of(651, 700);
    }

    @Override
    protected NumRange unavailable() {
        return NumRange.of(701, 750);
    }

    @Override
    protected NumRange deadlineExceeded() {
        return NumRange.of(751, 800);
    }
}
