package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.DigitCodedCase.Factory;

/**
 * A user can extend this class to define application-specific error cases.
 *
 * <p>Deprecated, please use {@link BasicDigitCodedCaseFactory} instead.
 */
@Deprecated
public abstract class ExtensibleCaseFactory extends BasicDigitCodedCaseFactory {
    protected ExtensibleCaseFactory(Factory caseFactory) {
        super(caseFactory);
    }
}
