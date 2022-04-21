package com.github.ikonglong.common.status.example;

import com.github.ikonglong.common.status.Case;
import com.github.ikonglong.common.status.DigitCodedCase;
import com.github.ikonglong.common.status.MultiModuleCaseFactory;
import com.github.ikonglong.common.status.Status;
import com.github.ikonglong.common.status.example.MultiModuleAppExample.ModuleA.ServiceA;
import com.github.ikonglong.common.status.example.MultiModuleAppExample.ModuleB.ServiceB;

/**
 * This example demonstrates how to use or extend this lib to define response codes for each module
 * of a multi-module application.
 */
public class MultiModuleAppExample {

  public static void main(String[] args) {
    // Simulates the logic of a client which calls multi services exposed by this app in one
    // use-case.
    try {
      ServiceA serviceA = new ServiceA();
      serviceA.doSomething("y");
      ServiceB serviceB = new ServiceB();
      serviceB.doSomething("x");
    } catch (ApplicationException e) {
      Status status = e.status();
      DigitCodedCase theCase = (DigitCodedCase) status.theCase();
      if (theCase.conditionCode() == 1) { // general code for invalid argument
        // Simulates showing a message on the UI to user
        System.out.println("Invalid input");
      } else if (theCase.moduleCode() == 1) {
        if (theCase.conditionCode() == 2) {
          System.out.println("The size of uploaded file is over 5M");
        } else if (theCase.conditionCode() == 3) {
          System.out.println("Uploaded file was damaged");
        }
      } else if (theCase.moduleCode() == 2) {
        if (theCase.conditionCode() == 2) {
          System.out.println("Comment contains malicious content");
        }
      }
    }
  }

  static class ModuleA {

    static class ErrorCases {

      private static final MultiModuleCaseFactory CODE_FACTORY = new MultiModuleCaseFactory(1);

      static final Case ILLEGAL_INPUT = CODE_FACTORY.firstInvalidArgument();
      static final Case FILE_LIMIT_EXCEEDED = CODE_FACTORY.newInvalidArgumentWithOffset(1);
      static final Case FILE_DAMAGED = CODE_FACTORY.newInvalidArgumentWithOffset(2);
    }

    static class ServiceA {
      void doSomething(String params) {
        if (params.equals("x")) {
          throw new ApplicationException(
              Status.INVALID_ARGUMENT.withCase(ErrorCases.ILLEGAL_INPUT, "Param p is invalid"));
        } else if (params.equals("y")) {
          throw new ApplicationException(
              Status.INVALID_ARGUMENT.withCase(
                  ErrorCases.FILE_LIMIT_EXCEEDED, "Uploaded file is too large"));
        }
        // Do something
      }
    }
  }

  public static class ModuleB {

    static class ErrorCases {

      private static final MultiModuleCaseFactory CODE_FACTORY = new MultiModuleCaseFactory(2);

      static final Case ILLEGAL_INPUT = CODE_FACTORY.firstInvalidArgument();
      static final Case CONTAINING_MALICIOUS_CONTENT = CODE_FACTORY.newInvalidArgumentWithOffset(1);
    }

    static class ServiceB {
      void doSomething(String params) {
        if (params.equals("x")) {
          throw new ApplicationException(
              Status.INVALID_ARGUMENT.withCase(ErrorCases.ILLEGAL_INPUT, "Param p is invalid"));
        } else if (params.equals("y")) {
          throw new ApplicationException(
              Status.INVALID_ARGUMENT.withCase(
                  ErrorCases.CONTAINING_MALICIOUS_CONTENT, "Comment contains malicious content"));
        }
        // Do something
      }
    }
  }
}
