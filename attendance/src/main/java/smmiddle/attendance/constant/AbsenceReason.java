package smmiddle.attendance.constant;

import lombok.Getter;

@Getter
public enum AbsenceReason {
  LATE("늦잠"),
  NO_CONTACT("연락X"),
  HEALTH_ISSUE("질병"),
  LESSON("학원"),
  TRIP("여행"),
  LONG_TERM("장결자"),
  OTHER("기타");

  private final String displayName;

  AbsenceReason(String displayName) {
    this.displayName = displayName;
  }
}


