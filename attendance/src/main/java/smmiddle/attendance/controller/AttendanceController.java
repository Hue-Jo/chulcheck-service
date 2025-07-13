package smmiddle.attendance.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.dto.AttendanceSummaryDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.repository.AttendanceRepository;
import smmiddle.attendance.service.AttendanceService;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

  private final AttendanceService attendanceService;
  private final AttendanceRepository attendanceRepository;

  // 첫 화면에서 셀 목록 조회
  @GetMapping("/")
  public String showCellSelectForm(Model model) {

    List<Cell> cells = attendanceService.getAllCells();
    LocalDate today = LocalDate.now();
    AttendanceSummaryDto summary = attendanceService.getAttendanceSummary(today);

    // 마지막 수정된 출석 가져오기
    Optional<Attendance> latest = attendanceService.getLatestAttendance();
    latest.ifPresent(att -> {
      model.addAttribute("lastUpdatedTime", att.getUpdatedDate());
      model.addAttribute("lastUpdatedCellName", att.getStudent().getCell().getName());
    });

    boolean isSunday = today.getDayOfWeek() == DayOfWeek.SATURDAY; // 일요일만

    model.addAttribute("cells", cells);
    model.addAttribute("today", today);
    model.addAttribute("isSunday", isSunday);
    model.addAttribute("attendanceStatusMap", summary.getAttendanceStatusMap()); // 출석 여부 map
    model.addAttribute("allSubmitted", summary.isAllSubmitted());
    model.addAttribute("todayPresentCount", summary.getTodayPresentCount());
    return "select_cell";
  }

  // 선택한 셀의 출석체크 폼
  @GetMapping("/attendance/form")
  public String showAttendanceForm(@RequestParam Long cellId, Model model) {
    Cell cell = attendanceService.getCellById(cellId);
    List<Student> students = attendanceService.getAllStudentsByCellId(cellId);

    model.addAttribute("cell", cell);
    model.addAttribute("students", students);
    model.addAttribute("today", LocalDate.now());
    model.addAttribute("absenceReasons", AbsenceReason.values());
    model.addAttribute("attendanceMap", Collections.emptyMap());

    return "attendance_form";
  }

  // 출석폼 제출 -> 첫 페이지로 리다이렉트
  @PostMapping("/attendance/submit")
  public String submitAttendanceForm(
      @RequestParam Long cellId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes
  ) {

    boolean exists = attendanceRepository.existsByStudent_Cell_IdAndDate(cellId, date);

    if (exists) {
      attendanceService.updateAttendance(request, cellId, date);
      redirectAttributes.addFlashAttribute("success", "✅ 출석 정보가 수정되었습니다.");
    } else {
      attendanceService.saveAttendance(request, cellId, date);
      redirectAttributes.addFlashAttribute("success", "✅ 성공적으로 제출되었습니다.");
    }
    return "redirect:/";
  }

  @GetMapping("/attendance/edit")
  public String showEditAttendanceForm(@RequestParam Long cellId, Model model) {
    Cell cell = attendanceService.getCellById(cellId);
    List<Student> students = attendanceService.getAllStudentsByCellId(cellId);
    Map<Long, Attendance> existingAttendanceMap = attendanceService.getAttendanceMap(cellId, LocalDate.now());

    model.addAttribute("cell", cell);
    model.addAttribute("students", students);
    model.addAttribute("today", LocalDateTime.now());
    model.addAttribute("attendanceMap", existingAttendanceMap);
    model.addAttribute("isEdit", true); // 수정 여부 표시
    return "attendance_form";
  }

  @GetMapping("/attendance/records")
  public String viewAttendanceRecords(
      @RequestParam(name = "cellId", required = false) Long cellId,
      @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
      Model model) {

    // 셀 목록 전달 (셀 선택용 드롭다운)
    List<Cell> cells = attendanceService.getAllCells();
    model.addAttribute("cells", cells);

    // 날짜 목록 전달 (출석 기록 있는 날짜만)
    List<LocalDate> attendanceDates = attendanceService.getAllAttendanceDates();
    model.addAttribute("dates", attendanceDates);

    // 셀이나 날짜가 선택되지 않았다면 조회 결과는 null (폼만 보여줌)
    if (cellId == null || cellId == 0 || date == null) {
      return "attendance_records";
    }

    // 출석 기록 조회
    List<Attendance> attendances = attendanceService.getAttendancesByCellIdAndDate(cellId, date);
    long presentCount = attendances.stream()
        .filter(att -> att.getStatus() == AttendanceStatus.PRESENT)
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

