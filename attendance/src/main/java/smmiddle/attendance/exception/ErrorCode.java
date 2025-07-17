package smmiddle.attendance.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  CELL_NOT_FOUND("존재하지 않는 셀입니다.", HttpStatus.NOT_FOUND),
  INVALID_CELL_ID("유효하지 않은 셀입니다", HttpStatus.BAD_REQUEST);

  private final String message;
  private final HttpStatus httpStatus;

}
