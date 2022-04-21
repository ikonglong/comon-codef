package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.CodingStrategy;
import com.github.ikonglong.common.status.DigitCodedCase.Factory;

/**
 * This code factory is for an application which consists of multiple(<10) modules.
 *
 * <p>Deprecated, please use {@link BasicDigitCodedCaseFactory.FactoryForMultiModuleApp} instead.
 */
@Deprecated
public class MultiModuleCaseFactory extends ExtensibleCaseFactory {

    public MultiModuleCaseFactory(int moduleCode) {
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
