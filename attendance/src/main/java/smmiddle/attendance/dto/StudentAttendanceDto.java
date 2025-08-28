package smmiddle.attendance.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.entity.Attendance;

@Getter
@Builder
public class StudentAttendanceDto {

  private Long id;
  private LocalDate date;             // 출석 날짜
  private AttendanceStatus status;    // 출석 상태 (PRESENT, ABSENT, LATE)
  private AbsenceReason absenceReason; // 결석 사유
  private String customReason;

  public static StudentAttendanceDto fromEntity(Attendance attendance) {
    return StudentAttendanceDto.builder()
        .id(attendance.getId())
        .date(attendance.getDate())
        .status(attendance.getStatus())
        .absenceReason(attendance.getAbsenceReason())
        .customReason(attendance.getCustomReason())
        .build();
  }

}
