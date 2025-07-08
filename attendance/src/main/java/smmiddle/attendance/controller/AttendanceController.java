package smmiddle.attendance.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
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
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.service.AttendanceService;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

  private final AttendanceService attendanceService;

  // 첫 화면에서 셀 목록 조회
  @GetMapping("/")
  public String showCellSelectForm(Model model) {

    List<Cell> cells = attendanceService.getAllCells();
    LocalDate today = LocalDate.now();

    Map<Long, Boolean> attendanceStatusMap = new HashMap<>();
    for (Cell cell : cells) {
      boolean submitted = attendanceService.alreadySubmitted(cell.getId(), today);
      attendanceStatusMap.put(cell.getId(), submitted);
    }

    boolean isSunday = today.getDayOfWeek() == DayOfWeek.WEDNESDAY;

    model.addAttribute("cells", cells);
    model.addAttribute("attendanceStatusMap", attendanceStatusMap); // 출석 여부 map
    model.addAttribute("today", today);
    model.addAttribute("isSunday", isSunday);
    return "select_cell";
  }

  // 선택한 셀의 출석체크 폼으로 이동
  @GetMapping("/attendance/form")
  public String showAttendanceForm(
      @RequestParam Long cellId, Model model) {
    Cell cell = attendanceService.getCellById(cellId);
    List<Student> students = attendanceService.getAllStudentsByCellId(cellId);

    model.addAttribute("cell", cell);
    model.addAttribute("students", students);
    model.addAttribute("today", LocalDate.now());
    return "attendance_form";
  }

  // 출첵 폼에서 제출된 데이터를 받아 출석 정보 저장후 성공 페이지로 리다이렉트
  @PostMapping("/attendance/submit")
  public String submitAttendanceForm(
      @RequestParam Long cellId,
      @RequestParam String date,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes
  ) {
    LocalDate attendanceDate = LocalDate.parse(date);

    if (attendanceService.alreadySubmitted(cellId, attendanceDate)) {
      redirectAttributes.addFlashAttribute("error", "⚠️ 변동사항은 부장님께 연락주세요.");
      return "redirect:/";
    }

    attendanceService.saveAttendance(request, cellId, attendanceDate);
    redirectAttributes.addFlashAttribute("success", "✅ 성공적으로 제출되었습니다.");
    return "redirect:/";
  }

}

