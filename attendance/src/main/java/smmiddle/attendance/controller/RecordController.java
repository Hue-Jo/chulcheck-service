package smmiddle.attendance.controller;

import static smmiddle.attendance.component.SessionUtil.isNotAuthenticated;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.dto.CellAttendanceSummaryDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.service.AttendanceService;

@Controller
@RequiredArgsConstructor
public class RecordController {

  private final AttendanceService attendanceService;


  @GetMapping("/attendance/records")
  public String viewAttendanceRecords(
      HttpSession session,
      @RequestParam(name = "cellId", required = false) Long cellId,
      @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
      Model model) {

    if (isNotAuthenticated(session)) {
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

    // 셀 목록 전달 (셀 선택용 드롭다운)
    List<Cell> cells = attendanceService.getAllCells();
    model.addAttribute("cells", cells);

    // 날짜 목록 전달 (출석 기록 있는 날짜만)
    List<LocalDate> attendanceDates = attendanceService.getAllAttendanceDates();
    model.addAttribute("dates", attendanceDates);

    // 날짜 또는 셀이 선택되지 않은 경우, 전체 셀 출결 요약
    if (cellId == null || cellId == 0 || date == null) {
      List<CellAttendanceSummaryDto> summaryList = attendanceService.getTodayCellAttendanceSummary();
      model.addAttribute("summaryList", summaryList);
      return "attendance_records";
    }

    // 출석 기록 조회
    List<Attendance> attendances = attendanceService.getAttendancesByCellIdAndDate(cellId, date);
    int presentCount = (int) attendances.stream()
        .filter(attendanceService::countsAsPresent)
        .count();

    // 셀 이름 (선택한 셀 ID로 조회)
    String selectedCellName = attendanceService.getCellNameById(cellId);

    model.addAttribute("attendances", attendances);
    model.addAttribute("presentCount", presentCount);
    model.addAttribute("selectedCellName", selectedCellName);
    model.addAttribute("selectedDate", date.toString());

    return "attendance_records";
  }
}
