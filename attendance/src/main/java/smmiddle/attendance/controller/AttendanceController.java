package smmiddle.attendance.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.dto.AllAttendanceSummaryDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.service.AttendanceService;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

  private final AttendanceService attendanceService;


  private boolean isNotAuthenticated(HttpSession session) {
    Boolean authenticated = (Boolean) session.getAttribute("authenticated");
    return authenticated == null || !authenticated;
  }

  // 첫 화면에서 셀 목록 조회
  @GetMapping("/")
  public String showCellSelectForm(HttpSession session, Model model) {
    if (isNotAuthenticated(session)) {
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

    List<Cell> cells = attendanceService.getAllCells();
    LocalDate today = LocalDate.now();
    boolean isSunday = today.getDayOfWeek() == DayOfWeek.FRIDAY; // 일요일만

    AllAttendanceSummaryDto summary = attendanceService.getAttendanceSummary(today);

    // 마지막 수정된 출석 가져오기
    attendanceService.getLatestAttendance().ifPresent(att -> {
      model.addAttribute("lastUpdatedTime", att.getUpdatedDate());
      model.addAttribute("lastUpdatedCellName", att.getStudent().getCell().getName());
    });

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
  public String showAttendanceForm(
      HttpSession session,
      @RequestParam Long cellId, Model model) {

    if (isNotAuthenticated(session)) {
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

    Cell cell = attendanceService.getCellById(cellId);
    List<Student> students = attendanceService.getAllStudentsByCellId(cellId);

    model.addAttribute("cell", cell);
    model.addAttribute("students", students);
    model.addAttribute("today", LocalDate.now());
    model.addAttribute("absenceReasons", AbsenceReason.values());
    model.addAttribute("attendanceMap", Collections.emptyMap());

    return "attendance_form";
  }


  // 출석폼 제출/수정
  @PostMapping("/attendance/submit")
  public String submitAttendanceForm(
      @RequestParam Long cellId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes
  ) {
    String result = attendanceService.submitAttendance(request, cellId, date);
    redirectAttributes.addFlashAttribute("success", "✅ 출석 정보가 " + result + "되었습니다.");
    return "redirect:/";
  }


  @GetMapping("/attendance/edit")
  public String showEditAttendanceForm(
      HttpSession session,
      @RequestParam Long cellId, Model model) {

    if (isNotAuthenticated(session)) {
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

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

}

