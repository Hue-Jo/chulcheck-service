package smmiddle.attendance.controller;

import static smmiddle.attendance.component.SessionUtil.isNotAuthenticated;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smmiddle.attendance.dto.CellAttendanceSummaryDto;
import smmiddle.attendance.dto.RecordDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.service.AttendanceService;
import smmiddle.attendance.service.RecordService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RecordController {

  private final AttendanceService attendanceService;
  private final RecordService recordService;


  @GetMapping("/attendance/records")
  public String viewAttendanceRecords(
      HttpSession session,
      @RequestParam(name = "date", required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
      Model model) {

    if (isNotAuthenticated(session)) {
      log.warn("비인증 사용자가 접근 시도");
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

    // 셀 목록 전달 (셀 선택용 드롭다운)
    List<Cell> cells = attendanceService.getAllCells();
    model.addAttribute("cells", cells);

    // 날짜 목록 전달 (출석 기록 있는 날짜만)
    List<LocalDate> attendanceDates = recordService.getAllAttendanceDates();
    model.addAttribute("dates", attendanceDates);

    // 날짜 또는 셀이 선택되지 않은 경우, 전체 셀 출결 요약
    if (date == null) {
      List<CellAttendanceSummaryDto> summaryList = recordService.getTodayCellAttendanceSummary();
      model.addAttribute("summaryList", summaryList);
      return "attendance_records";
    }

    Map<Cell, RecordDto> cellAttendanceMap = recordService.getAllCellsAttendanceByDateWithCount(date);
    model.addAttribute("cellAttendanceMap", cellAttendanceMap);
    model.addAttribute("selectedDate", date.toString());

    long totalPresentAndAllowedCount = recordService.getTotalPresentAndAllowedCountByDate(date);
    model.addAttribute("totalPresentAndAllowedCount", totalPresentAndAllowedCount);

    return "attendance_records";
  }
}
