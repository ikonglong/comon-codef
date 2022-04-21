package com.github.ikonglong.common.status.example;

import com.github.ikonglong.common.status.Status;

public class ApplicationException extends RuntimeException {

  private Status status;

  public ApplicationException(Status status) {
    super(status.message());
    this.status = status;
  }

  public Status status() {
    return status;
  }
}
