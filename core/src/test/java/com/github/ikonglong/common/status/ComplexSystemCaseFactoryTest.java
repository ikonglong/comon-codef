package com.github.ikonglong.common.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory.FactoryForComplexSystem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexSystemCaseFactoryTest {

  FactoryForComplexSystem caseFactory = new FactoryForComplexSystem(9, 2);

  @Test
  public void generalCodes() {
    Assertions.assertThat(caseFactory.firstOk()).hasToString("9_2_000");
    Assertions.assertThat(caseFactory.firstInvalidArgument()).hasToString("9_2_001");
    Assertions.assertThat(caseFactory.firstFailedPrecondition()).hasToString("9_2_051");
    Assertions.assertThat(caseFactory.firstOutOfRange()).hasToString("9_2_101");
    Assertions.assertThat(caseFactory.firstUnauthenticated()).hasToString("9_2_151");
    Assertions.assertThat(caseFactory.firstPermissionDenied()).hasToString("9_2_201");
    Assertions.assertThat(caseFactory.firstNotFound()).hasToString("9_2_251");
    Assertions.assertThat(caseFactory.firstAlreadyExists()).hasToString("9_2_301");
    Assertions.assertThat(caseFactory.firstAborted()).hasToString("9_2_351");
    Assertions.assertThat(caseFactory.firstResourceExhausted()).hasToString("9_2_401");
    Assertions.assertThat(caseFactory.firstCancelled()).hasToString("9_2_451");
    Assertions.assertThat(caseFactory.firstDataLoss()).hasToString("9_2_501");
    Assertions.assertThat(caseFactory.firstUnknownError()).hasToString("9_2_551");
    Assertions.assertThat(caseFactory.firstInternalError()).hasToString("9_2_601");
    Assertions.assertThat(caseFactory.firstUnimplemented()).hasToString("9_2_651");
    Assertions.assertThat(caseFactory.firstUnavailable()).hasToString("9_2_701");
    Assertions.assertThat(caseFactory.firstDeadLineExceeded()).hasToString("9_2_751");
  }

  @Test
  public void customConditionCodes() {
    // ok -> case code [0-0]
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(-1)).isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(1)).isInstanceOf(IllegalArgumentException.class);

    // invalid arg -> case code [1-50]
    Assertions.assertThat(caseFactory.newInvalidArgument(49)).hasToString("9_2_049");
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(0))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(51))
        .isInstanceOf(IllegalArgumentException.class);

    // resource exhausted -> case code [401, 450]
    Assertions.assertThat(caseFactory.newResourceExhausted(401)).hasToString("9_2_401");
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(400))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(451))
        .isInstanceOf(IllegalArgumentException.class);

    // unsupported operation -> case code [651, 700]
    Assertions.assertThat(caseFactory.newUnimplemented(689)).hasToString("9_2_689");
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(1))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(800))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
