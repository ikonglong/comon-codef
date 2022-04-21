package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.CodingStrategy;
import com.github.ikonglong.common.status.DigitCodedCase.Factory;

/**
 * This code factory is for a monolithic application.
 *
 * <p>Deprecated, please use {@link BasicDigitCodedCaseFactory.FactoryForMonolithicApp} instead.
 */
@Deprecated
public class MonolithicCaseFactory extends ExtensibleCaseFactory {

    public MonolithicCaseFactory() {
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
