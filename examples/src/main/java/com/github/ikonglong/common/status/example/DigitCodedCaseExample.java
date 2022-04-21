package com.github.ikonglong.common.status.example;

import com.github.ikonglong.common.status.BasicDigitCodedCaseFactory;
import com.github.ikonglong.common.status.Case;
import com.github.ikonglong.common.status.DigitCodedCase.CodingStrategy;
import com.github.ikonglong.common.status.DigitCodedCase.Factory;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.StatusCodeToNumRangeMapper;
import com.github.ikonglong.common.status.example.DigitCodedCaseExample.OrderModule.ErrorCases;

public class DigitCodedCaseExample {

  public static void main(String[] args) {
    System.out.println(ErrorCases.INSUFFICIENT_INVENTORY);
  }

  static class CustomCaseFactory extends BasicDigitCodedCaseFactory {
    public CustomCaseFactory(int appCode, int moduleCode) {
      super(
          new Factory.Builder()
              .codingStrategy(
                  CodingStrategy.newBuilder()
                      .numDigitsOfAppCode(2)
                      .numDigitsOfModuleCode(2)
                      .numDigitsOfConditionCode(3)
                      .statusCodeMapper(new StatusCodeToNumRangeMapper())
                      .build())
              .appCode(appCode)
              .moduleCode(moduleCode)
              .build());
    }
  }

  static class OrderModule {
    static final CustomCaseFactory caseFactory = new CustomCaseFactory(1, 1);

    static class OrderService {
      void placeOrder(String item, int count) {
        if (item.equals("-")) {
          throw new ApplicationException(
              Status.INVALID_ARGUMENT.withMessage("Invalid item id: %s", item));
        }
        if (count > 10) {
          throw new ApplicationException(
              Status.FAILED_PRECONDITION.withCase(
                  ErrorCases.PURCHASE_LIMIT_EXCEEDED, "Purchase limit[=%s] exceeded", 10));
        }

        int inventory = 5;
        if (inventory < count) {
          throw new ApplicationException(
              Status.FAILED_PRECONDITION.withCase(
                  ErrorCases.INSUFFICIENT_INVENTORY,
                  "Insufficient inventory[=%s] of item[=%s]",
                  count,
                  item));
        }

        System.out.println("Do biz logic: placing order");
      }
    }

    static class ErrorCases {
      static final Case PURCHASE_LIMIT_EXCEEDED = caseFactory.firstFailedPrecondition();
      static final Case INSUFFICIENT_INVENTORY = caseFactory.newFailedPreconditionWithOffset(1);
    }
  }

  static class UserModule {
    static final CustomCaseFactory caseFactory = new CustomCaseFactory(1, 2);

    static class UserService {
      void login(String username, String password) {
        if ("x".equals(password)) {
          throw new ApplicationException(
              Status.FAILED_PRECONDITION.withCase(ErrorCases.INCORRECT_LOGIN_CREDENTIAL));
        }

        if ("-".equals(username)) {
          throw new ApplicationException(
              Status.FAILED_PRECONDITION.withCase(
                  ErrorCases.USER_DISABLED, "The user is disabled"));
        }

        System.out.println("Do biz logic: authenticate the identity of the user");
      }
    }

    static class ErrorCases {
      static final Case INCORRECT_LOGIN_CREDENTIAL = caseFactory.firstFailedPrecondition();
      static final Case USER_DISABLED = caseFactory.newFailedPreconditionWithOffset(1);
    }
  }
}
