package smmiddle.attendance.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    model.addAttribute("cells", attendanceService.getAllCells());
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
  
  // 출켁 성공 화면

}
