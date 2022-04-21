package com.github.ikonglong.common.status.example;

import com.github.ikonglong.common.status.Status;

public class IllegalInputException extends ApplicationException {

  public IllegalInputException(Status status) {
    super(status);
  }
}
