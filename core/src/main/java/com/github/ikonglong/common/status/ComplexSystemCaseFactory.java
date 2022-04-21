package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.CodingStrategy;
import com.github.ikonglong.common.status.DigitCodedCase.Factory;

/**
 * This code factory is for a system which consists of multiple(<10) applications, and some or all
 * of the apps consist of multiple(<10) modules.
 *
 * <p>Deprecated, please use {@link BasicDigitCodedCaseFactory.FactoryForComplexSystem} instead.
 */
@Deprecated
public class ComplexSystemCaseFactory extends ExtensibleCaseFactory {

    public ComplexSystemCaseFactory(int appCode, int moduleCode) {
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
