package smmiddle.attendance.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smmiddle.attendance.dto.StudentDto;
import smmiddle.attendance.repository.StudentRepository;
import smmiddle.attendance.service.AttendanceService;
import smmiddle.attendance.service.SearchService;

@Controller
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;
  private final AttendanceService attendanceService;

  /**
   * 학생 검색 폼
   */
  @GetMapping("/search")
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
    model.addAttribute("students", students);
    model.addAttribute("keyword", name);
    return "students";
  }


}
