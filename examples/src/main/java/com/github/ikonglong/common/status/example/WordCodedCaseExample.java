package com.github.ikonglong.common.status.example;

import static java.util.Objects.requireNonNull;

import com.github.ikonglong.common.status.Case;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.Status.Code;

public class WordCodedCaseExample {

  static class OrderService {

    /** Returns status directly. */
    Status placeOrderV1(String item, int count) {
      if (item.equals("-")) {
        return Status.INVALID_ARGUMENT.withMessage("Invalid item id: %s", item);
      }
      if (count > 10) {
        return Status.FAILED_PRECONDITION.withCase(
            ErrorCase.PURCHASE_LIMIT_EXCEEDED, "Purchase limit[=%s] exceeded", 10);
      }

      int inventory = 5;
      if (inventory < count) {
        return Status.FAILED_PRECONDITION.withCase(
            ErrorCase.INSUFFICIENT_INVENTORY,
            "Insufficient inventory[=%s] of item[=%s]",
            count,
            item);
      }

      System.out.println("Do biz logic: placing order");
      return Status.OK;
    }

    /** Throws an {@link Exception} wrapping a error status. */
    void placeOrderV2(String item, int count) {
      if (item.equals("-")) {
        throw new ApplicationException(
            Status.INVALID_ARGUMENT.withMessage("Invalid item id: %s", item));
      }
      if (count > 10) {
        throw new ApplicationException(
            Status.FAILED_PRECONDITION.withCase(
                ErrorCase.PURCHASE_LIMIT_EXCEEDED, "Purchase limit[=%s] exceeded", 10));
      }

      int inventory = 5;
      if (inventory < count) {
        throw new ApplicationException(
            Status.FAILED_PRECONDITION.withCase(
                ErrorCase.INSUFFICIENT_INVENTORY,
                "Insufficient inventory[=%s] of item[=%s]",
                count,
                item));
      }

      System.out.println("Do biz logic: placing order");
    }
  }

  enum ErrorCase implements Case {
    PURCHASE_LIMIT_EXCEEDED(Code.FAILED_PRECONDITION),
    INSUFFICIENT_INVENTORY(Code.FAILED_PRECONDITION);

    private final Status.Code statusCode;

    ErrorCase(Status.Code statusCode) {
      this.statusCode = requireNonNull(statusCode, "statusCode");
    }

    @Override
    public String identifier() {
      return this.name();
    }

    @Override
    public Status.Code statusCode() {
      return statusCode;
    }
  }
}
