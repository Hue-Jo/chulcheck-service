package smmiddle.attendance.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 전체 집계를 위한 DTO
@Getter
@AllArgsConstructor
public class AllAttendanceSummaryDto {
  private final Map<Long, Boolean> attendanceStatusMap; // 셀별 출석 제출 여부
  private final boolean allSubmitted; // 전체 셀 출석부 제출 여부
  private List<String> unsubmittedCells; // 출석부 미제출 셀 리스트
  private final int todayPresentCount; // 오늘 총 출석수

}
