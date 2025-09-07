package smmiddle.attendance.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smmiddle.attendance.dto.CellAttendanceSummaryDto;
import smmiddle.attendance.dto.RecordDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.repository.AttendanceRepository;
import smmiddle.attendance.repository.CellRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService {

  private final AttendanceService attendanceService;
  private final AttendanceRepository attendanceRepository;
  private final CellRepository cellRepository;

  /**
   * 출석이 저장된 모든 날짜 목록 조회
   */
  public List<LocalDate> getAllAttendanceDates() {
    return attendanceRepository.findDistinctDates();
  }


  /**
   * 특정 날짜의 모든 셀의 출결사항 조회 & 각 셀의 총 출석수 조회
   */
  public Map<Cell, RecordDto> getAllCellsAttendanceByDateWithCount(LocalDate date) {
    List<Cell> cells = cellRepository.findAll();
    Map<Cell, RecordDto> result = new LinkedHashMap<>();

    for (Cell cell : cells) {
      List<Attendance> attList = attendanceService.getAttendancesByCellIdAndDate(cell.getId(), date);
      long presentCount = attList.stream()
          .filter(attendanceService::countsAsPresent)
          .count();
      result.put(cell, new RecordDto(attList, presentCount));
    }

    return result;
  }

//  /**
//   * 특정 날짜의 전체 출석 합계
//   */
//  public long getTotalPresentAndAllowedCountByDate(LocalDate date) {
//    List<Attendance> attendances = attendanceRepository.findByDate(date);
//    return attendances.stream()
//        .filter(attendanceService::countsAsPresent)
//        .count();
//  }

  /**
   * 출결정보 조회 화면의 요약본
   */
  public List<CellAttendanceSummaryDto> getTodayCellAttendanceSummary() {
    LocalDate today = LocalDate.now();
    List<Cell> cells = cellRepository.findAll();
    List<CellAttendanceSummaryDto> summaries = new ArrayList<>();

    for (Cell cell : cells) {
      boolean submitted = attendanceRepository.existsByStudent_Cell_IdAndDate(cell.getId(), today);

      String presentCountDisplay;
      if (submitted) {
        List<Attendance> attendances = attendanceRepository.findByStudent_Cell_IdAndDateOrderByStudent_NameAsc(cell.getId(), today);
        long presentCount = attendances.stream()
            .filter(attendanceService::countsAsPresent)
            .count();
        presentCountDisplay = presentCount + "명";
      } else {
        presentCountDisplay = "⌛";
      }

      summaries.add(new CellAttendanceSummaryDto(cell.getName(), submitted, presentCountDisplay));
    }

    return summaries;
  }

}
