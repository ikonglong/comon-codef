package com.github.ikonglong.common.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory.FactoryForMonolithicApp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MonolithicCaseFactoryTest {

  FactoryForMonolithicApp caseFactory = new FactoryForMonolithicApp();

  @Test
  public void generalCodes() {
    Assertions.assertThat(caseFactory.firstOk()).hasToString("000");
    Assertions.assertThat(caseFactory.firstInvalidArgument()).hasToString("001");
    Assertions.assertThat(caseFactory.firstFailedPrecondition()).hasToString("051");
    Assertions.assertThat(caseFactory.firstOutOfRange()).hasToString("101");
    Assertions.assertThat(caseFactory.firstUnauthenticated()).hasToString("151");
    Assertions.assertThat(caseFactory.firstPermissionDenied()).hasToString("201");
    Assertions.assertThat(caseFactory.firstNotFound()).hasToString("251");
    Assertions.assertThat(caseFactory.firstAlreadyExists()).hasToString("301");
    Assertions.assertThat(caseFactory.firstAborted()).hasToString("351");
    Assertions.assertThat(caseFactory.firstResourceExhausted()).hasToString("401");
    Assertions.assertThat(caseFactory.firstCancelled()).hasToString("451");
    Assertions.assertThat(caseFactory.firstDataLoss()).hasToString("501");
    Assertions.assertThat(caseFactory.firstUnknownError()).hasToString("551");
    Assertions.assertThat(caseFactory.firstInternalError()).hasToString("601");
    Assertions.assertThat(caseFactory.firstUnimplemented()).hasToString("651");
    Assertions.assertThat(caseFactory.firstUnavailable()).hasToString("701");
    Assertions.assertThat(caseFactory.firstDeadLineExceeded()).hasToString("751");
  }

  @Test
  public void customConditionCodes() {
    // ok -> case code [0-0]
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(-1)).isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newOk(1)).isInstanceOf(IllegalArgumentException.class);

    // invalid arg -> case code [1-50]
    Assertions.assertThat(caseFactory.newInvalidArgument(49)).hasToString("049");
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(0))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newInvalidArgument(51))
        .isInstanceOf(IllegalArgumentException.class);

    // resource exhausted -> case code [401, 450]
    Assertions.assertThat(caseFactory.newResourceExhausted(401)).hasToString("401");
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(400))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newResourceExhausted(451))
        .isInstanceOf(IllegalArgumentException.class);

    // unsupported operation -> case code [651, 700]
    Assertions.assertThat(caseFactory.newUnimplemented(689)).hasToString("689");
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(1))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> caseFactory.newUnimplemented(800))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
