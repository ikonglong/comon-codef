package com.github.ikonglong.common.status.example;

import com.google.common.base.MoreObjects;
import com.github.ikonglong.common.status.Case;
import com.github.ikonglong.common.status.MonolithicCaseFactory;
import com.github.ikonglong.common.status.Status;

/**
 * This example demonstrates how to use or extend this lib to define response codes in a monolithic
 * application.
 */
public class MonolithicAppExample {

  public static void main(String[] args) {
    System.out.println("Restful requests");
    System.out.println("----------------------------");
    RestfulApi api = new RestfulApi();
    System.out.println("Response for upload: " + api.uploadFile("large"));
    System.out.println("Response for addToShoppingCart: " + api.addToShoppingCart("bad"));
    System.out.println("Response for upload: " + api.uploadFile("large"));

    System.out.println("\nDomain service");
    System.out.println("----------------------------");
    DomainService service = new DomainService();
    try {
      service.addToShoppingCart("sku-1", 6);
    } catch (ApplicationException e) {
      System.out.println("Failure for addToShoppingCart: " + e.status());
    }
    try {
      service.placeOrder("order-1");
    } catch (ApplicationException e) {
      System.out.println("Failure for placeOrder: " + e.status());
    }
  }

  public static class ErrorCases {

    static final MonolithicCaseFactory caseFactory = new MonolithicCaseFactory();

    // General code for ok
    static final Case OK = caseFactory.firstOk(); // 0

    // General code for invalid argument
    static final Case INVALID_ARG = caseFactory.firstInvalidArgument(); // 1
    // Specific code for file size exceeding limit
    static final Case FILE_LIMIT_EXCEEDED = caseFactory.newInvalidArgument(1); // 2

    // General code for failed precondition
    static final Case FAILED_PRECONDITION = caseFactory.firstFailedPrecondition(); // 51
    static final Case PURCHASE_LIMIT_EXCEEDED =
        caseFactory.newFailedPreconditionWithOffset(1); // 52
  }

  public static class RestfulApi {

    public Response uploadFile(String request) {
      if (request.equals("large")) {
        return new Response(
            Status.INVALID_ARGUMENT.withCase(
                ErrorCases.FILE_LIMIT_EXCEEDED, "File limit (%sM) exceeded", 50));
      }
      return new Response(Status.OK);
    }

    public Response addToShoppingCart(String request) {
      if (request.equals("bad")) {
        return new Response(
            Status.INVALID_ARGUMENT.withCase(
                ErrorCases.INVALID_ARG, "Some argument is valid: the reason"));
      }
      return new Response(Status.OK);
    }

    public static class Response {
      int httpCode;
      Status status;

      public Response(Status status) {
        this.status = status;
        this.httpCode = status.code().toHttpStatus().code();
      }

      @Override
      public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("httpCode", httpCode)
            .add("status", status)
            .toString();
      }
    }
  }

  public static class DomainService {

    public void addToShoppingCart(String sku, int count) {
      if (count > 5) {
        throw new ApplicationException(
            Status.FAILED_PRECONDITION.withCase(
                ErrorCases.PURCHASE_LIMIT_EXCEEDED, "Purchase limit [%s] exceeded", 5));
      }
      // domain logic
    }

    public void placeOrder(String order) {
      // Check some business constraints
      boolean stockShortage = true;
      if (stockShortage) {
        throw new ApplicationException(
            Status.FAILED_PRECONDITION.withCase(
                ErrorCases.PURCHASE_LIMIT_EXCEEDED, "Purchase limit [%s] exceeded", 5));
      }
      // domain logic
    }
  }
}
