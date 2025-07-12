package smmiddle.attendance.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceSummaryDto {
  private final Map<Long, Boolean> attendanceStatusMap; // 셀별 출석 제출 여부
  private final boolean allSubmitted; // 전체 셀 제출 여부
  private final int todayPresentCount; // 오늘 총 출석수

}
