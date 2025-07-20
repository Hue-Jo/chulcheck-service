package smmiddle.attendance.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smmiddle.attendance.service.StudentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

  private final StudentService studentService;

  @PostMapping("/add")
  @ResponseBody
  public Map<String, Object> addStudent(
      @RequestParam Long cellId,
      @RequestParam String studentName) {

    Map<String, Object> response = new HashMap<>();
    try {
      studentService.addNewStudent(cellId, studentName);
      response.put("success", true);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", e.getMessage());
    }
    return response;
  }
}
