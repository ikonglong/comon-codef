package com.github.ikonglong.common.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory.FactoryForMultiModuleApp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiModuleCaseFactoryTest {

  FactoryForMultiModuleApp caseFactory = new FactoryForMultiModuleApp(1);

  @Test
  public void generalCodes() {
    Assertions.assertThat(caseFactory.firstOk()).hasToString("1_000");
    Assertions.assertThat(caseFactory.firstInvalidArgument()).hasToString("1_001");
    Assertions.assertThat(caseFactory.firstFailedPrecondition()).hasToString("1_051");
    Assertions.assertThat(caseFactory.firstOutOfRange()).hasToString("1_101");
    Assertions.assertThat(caseFactory.firstUnauthenticated()).hasToString("1_151");
    Assertions.assertThat(caseFactory.firstPermissionDenied()).hasToString("1_201");
    Assertions.assertThat(caseFactory.firstNotFound()).hasToString("1_251");
    Assertions.assertThat(caseFactory.firstAlreadyExists()).hasToString("1_301");
    Assertions.assertThat(caseFactory.firstAborted()).hasToString("1_351");
    Assertions.assertThat(caseFactory.firstResourceExhausted()).hasToString("1_401");
    Assertions.assertThat(caseFactory.firstCancelled()).hasToString("1_451");
    Assertions.assertThat(caseFactory.firstDataLoss()).hasToString("1_501");
    Assertions.assertThat(caseFactory.firstUnknownError()).hasToString("1_551");
    Assertions.assertThat(caseFactory.firstInternalError()).hasToString("1_601");
    Assertions.assertThat(caseFactory.firstUnimplemented()).hasToString("1_651");
    Assertions.assertThat(caseFactory.firstUnavailable()).hasToString("1_701");
    Assertions.assertThat(caseFactory.firstDeadLineExceeded()).hasToString("1_751");
  }

  @Test
  public void customConditionCodes() {
    // ok -> case code [0-0]
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(-1)).isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(1)).isInstanceOf(IllegalArgumentException.class);

    // invalid arg -> case code [1-50]
    Assertions.assertThat(caseFactory.newInvalidArgument(49)).hasToString("1_049");
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(0))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(51))
        .isInstanceOf(IllegalArgumentException.class);

    // resource exhausted -> case code [401, 450]
    Assertions.assertThat(caseFactory.newResourceExhausted(401)).hasToString("1_401");
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(400))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(451))
        .isInstanceOf(IllegalArgumentException.class);

    // unsupported operation -> case code [651, 700]
    Assertions.assertThat(caseFactory.newUnimplemented(689)).hasToString("1_689");
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(1))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(800))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
