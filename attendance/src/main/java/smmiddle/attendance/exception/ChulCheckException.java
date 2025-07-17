package smmiddle.attendance.exception;

import lombok.Getter;

@Getter
public class ChulCheckException extends RuntimeException {

  private final ErrorCode errorCode;

  public ChulCheckException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }


}
