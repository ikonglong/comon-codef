package com.github.ikonglong.common.status;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.math.IntMath;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Represents a series of error cases of which the identifiers are digital codes. For example, you
 * can define a case with id '1_100' for exceeding the purchase limit.
 */
public class DigitCodedCase implements Case {

    private final int appCode;
    private final int moduleCode;
    private final int conditionCode;

    private final String caseId;

    private final transient Status.Code statusCode;

    DigitCodedCase(
            int appCode, int moduleCode, int conditionCode, String caseId, Status.Code statusCode) {
        checkArgument(appCode >= 0, "appCode < 0");
        checkArgument(moduleCode >= 0, "moduleCode < 0");
        checkArgument(conditionCode >= 0, "conditionCode < 0");
        this.appCode = appCode;
        this.moduleCode = moduleCode;
        this.conditionCode = conditionCode;
        this.caseId = requireNonNull(caseId, "caseId");
        this.statusCode = requireNonNull(statusCode, "statusCode");
    }

    public int appCode() {
        return appCode;
    }

    public int moduleCode() {
        return moduleCode;
    }

    public int conditionCode() {
        return conditionCode;
    }

    @Override
    public String identifier() {
        return caseId;
    }

    @Override
    public Status.Code statusCode() {
        return statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DigitCodedCase case1 = (DigitCodedCase) o;
        return appCode == case1.appCode
                && moduleCode == case1.moduleCode
                && conditionCode == case1.conditionCode
                && caseId.equals(case1.caseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appCode, moduleCode, conditionCode, caseId);
    }

    @Override
    public String toString() {
        return caseId;
    }

    public static class Factory {

        private final CodingStrategy codingStrategy;
        private final int appCode;
        private final int moduleCode;

        private Factory(CodingStrategy codingStrategy, int appCode, int moduleCode) {
            requireThatRangeIncludeCode(codingStrategy.appCodeRange, appCode, UseAs.APP_CODE);
            requireThatRangeIncludeCode(codingStrategy.moduleCodeRange, moduleCode, UseAs.MODULE_CODE);
            this.codingStrategy = codingStrategy;
            this.appCode = appCode;
            this.moduleCode = moduleCode;
        }

        public CodingStrategy codingStrategy() {
            return codingStrategy;
        }

        public DigitCodedCase create(Status.Code statusCode, int conditionCode) {
            requireNonNull(statusCode, "statusCode");
            checkArgument(
                    codingStrategy.statusCodeMapper.hasMappingFor(statusCode),
                    "Given CodeMapper doesn't contain status code %s",
                    statusCode.name());
            checkArgument(
                    codingStrategy
                            .statusCodeMapper
                            .conditionCodeSegmentFor(statusCode)
                            .include(conditionCode),
                    "Condition code segment %s for status code %s doesn't include given condition code %s",
                    codingStrategy.statusCodeMapper.conditionCodeSegmentFor(statusCode),
                    statusCode.name(),
                    conditionCode);

            StringBuilder caseId = new StringBuilder(20);
            if (codingStrategy.numDigitsOfAppCode > 0) {
                caseId.append(padLeftZeros(appCode, codingStrategy.numDigitsOfAppCode)).append("_");
            }
            if (codingStrategy.numDigitsOfModuleCode > 0) {
                caseId.append(padLeftZeros(moduleCode, codingStrategy.numDigitsOfModuleCode)).append("_");
            }
            caseId.append(padLeftZeros(conditionCode, codingStrategy.numDigitsOfConditionCode));
            return new DigitCodedCase(
                    appCode, moduleCode, conditionCode, caseId.toString(), statusCode);
        }

        private void requireThatRangeIncludeCode(NumRange range, int code, UseAs useAs) {
            checkArgument(range.include(code), "%s: range %s not include code %s", useAs, range, code);
        }

        private String padLeftZeros(int num, int minLen) {
            return Strings.padStart(String.valueOf(num), minLen, '0');
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("codingStrategy", codingStrategy)
                    .add("appCode", appCode)
                    .add("moduleCode", moduleCode)
                    .toString();
        }

        private enum UseAs {
            APP_CODE,
            MODULE_CODE,
            CONDITION_CODE
        }

        public static class Builder {
            private CodingStrategy codingStrategy;
            private int appCode = 0;
            private int moduleCode = 0;

            public Builder codingStrategy(CodingStrategy codingStrategy) {
                this.codingStrategy = requireNonNull(codingStrategy);
                return this;
            }

            public Builder appCode(int appCode) {
                checkArgument(appCode >= 0, "appCode < 0");
                this.appCode = appCode;
                return this;
            }

            public Builder moduleCode(int moduleCode) {
                checkArgument(moduleCode >= 0, "moduleCode < 0");
                this.moduleCode = moduleCode;
                return this;
            }

            public Factory build() {
                requireNonNull(codingStrategy, "codingStrategy");
                return new Factory(codingStrategy, appCode, moduleCode);
            }
        }
    }

    public static class CodingStrategy {

        private int numDigitsOfAppCode = -1; // `-1` indicates it is uninitialized
        private NumRange appCodeRange;

        private int numDigitsOfModuleCode = -1; // `-1` indicates it field is uninitialized
        private NumRange moduleCodeRange;

        private int numDigitsOfConditionCode;
        private NumRange conditionCodeRange;
        private StatusCodeMapper statusCodeMapper;

        private CodingStrategy() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public StatusCodeMapper statusCodeMapper() {
            return statusCodeMapper;
        }

        @Override
        public java.lang.String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("appCodeRange", appCodeRange)
                    .add("moduleCodeRange", moduleCodeRange)
                    .add("conditionCodeRange", conditionCodeRange)
                    .add("statusCodeMapper", "<omitted because too long>")
                    .toString();
        }

        public static class Builder {

            private CodingStrategy strategy;

            private Builder() {
                this.strategy = new CodingStrategy();
            }

            public Builder numDigitsOfAppCode(int numDigitsOfAppCode) {
                checkArgument(numDigitsOfAppCode >= 0, "numDigitsOfAppCode must be gte 0");
                this.strategy.numDigitsOfAppCode = numDigitsOfAppCode;
                return this;
            }

            public Builder numDigitsOfModuleCode(int numDigitsOfModuleCode) {
                checkArgument(numDigitsOfModuleCode >= 0, "moduleCodeDigitsNum must be gte 0");
                strategy.numDigitsOfModuleCode = numDigitsOfModuleCode;
                return this;
            }

            public Builder numDigitsOfConditionCode(int numDigitsOfConditionCode) {
                checkArgument(numDigitsOfConditionCode > 0, "numDigitsOfConditionCode must be gt 0");
                strategy.numDigitsOfConditionCode = numDigitsOfConditionCode;
                return this;
            }

            public Builder statusCodeMapper(StatusCodeMapper statusCodeMapper) {
                strategy.statusCodeMapper = requireNonNull(statusCodeMapper, "statusCodeMapper");
                return this;
            }

            public CodingStrategy build() {
                checkArgument(strategy.numDigitsOfAppCode > -1, "numDigitsOfAppCode is not given");
                checkArgument(strategy.numDigitsOfModuleCode > -1, "numDigitsOfModuleCode is not given");
                strategy.appCodeRange = NumRange.of(0, IntMath.pow(10, strategy.numDigitsOfAppCode) - 1);
                strategy.moduleCodeRange =
                        NumRange.of(0, IntMath.pow(10, strategy.numDigitsOfModuleCode) - 1);
                strategy.conditionCodeRange =
                        NumRange.of(0, IntMath.pow(10, strategy.numDigitsOfConditionCode) - 1);
                for (NumRange codeSegment : strategy.statusCodeMapper.conditionCodeSegments()) {
                    checkArgument(
                            strategy.conditionCodeRange.include(codeSegment),
                            "condition code range %s doesn't include condition code segment %s from given %s",
                            strategy.conditionCodeRange,
                            codeSegment,
                            strategy.statusCodeMapper.getClass().getSimpleName());
                }

                return strategy;
            }
        }
    }

    /**
     * A number range which includes the start and end.
     */
    public static class NumRange implements Comparable<NumRange> {

        private final int start;
        private final int end;

        private NumRange(int start, int end) {
            checkArgument(start >= 0, "start < 0");
            checkArgument(end >= 0, "end < 0");
            checkArgument(end >= start, "end < start");
            this.start = start;
            this.end = end;
        }

        public static NumRange of(int start, int end) {
            return new NumRange(start, end);
        }

        public int start() {
            return start;
        }

        public int end() {
            return end;
        }

        public boolean include(int code) {
            return start <= code && code <= end;
        }

        public boolean include(NumRange b) {
            return this.start <= b.start && b.end <= this.end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NumRange numRange = (NumRange) o;
            return start == numRange.start && end == numRange.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return format("[%d, %d]", start, end);
        }

        @Override
        public int compareTo(NumRange that) {
            return this.start - that.start;
        }
    }
}
