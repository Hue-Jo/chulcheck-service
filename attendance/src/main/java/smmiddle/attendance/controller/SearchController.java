package smmiddle.attendance.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smmiddle.attendance.dto.StudentAttendanceDto;
import smmiddle.attendance.dto.StudentDto;
import smmiddle.attendance.repository.StudentRepository;
import smmiddle.attendance.service.AttendanceService;
import smmiddle.attendance.service.SearchService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

  private final SearchService searchService;
  private final AttendanceService attendanceService;

  /**
   * 학생 검색 폼
   */
  @GetMapping
  public String showSearchForm() {
    return "search";
  }

  /**
   * 이름으로 학생 검색 결과 페이지
   */
  @GetMapping("/students")
  public String searchStudentsByName(
      @RequestParam String name, Model model
  ) {
    List<StudentDto> students = searchService.searchStudentByName(name);

    // 해당 이름을 가진 학생이 존재하지 않을 때
    if (students.isEmpty()) {
      model.addAttribute("message", "검색 결과가 없습니다.");
      return "search"; // 검색 화면으로 리턴
    }

    // 해당 이름을 가진 동명이인이 있을 때
    if (students.size() == 1) {
      Long studentId = students.get(0).getId();
      return "redirect:/search/students/" + studentId + "/attendance";
    }
    
    // 해당 이름을 가진 학생이 1명일 때
    model.addAttribute("students", students);
    return "search";
  }

  /**
   * 특정 학생의 1년 출석 조회
   */
  @GetMapping("/students/{studentId}/attendance")
  public String getYearlyAttendance(
      @PathVariable Long studentId, Model model) {

    return "until-now"; // 출석 상세 화면
  }


}
