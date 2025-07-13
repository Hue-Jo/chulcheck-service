package smmiddle.attendance.constant;

import lombok.Getter;

@Getter
public enum AbsenceReason {
  LATE("늦잠"),
  NO_CONTACT("연락 x"),
  HEALTH_ISSUE("건강 문제"),
  LESSON("학원/레슨"),
  OTHER("기타 (직접 작성)");

  private final String displayName;

  AbsenceReason(String displayName) {
    this.displayName = displayName;
  }
}


