package smmiddle.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import smmiddle.attendance.repository.CellRepository;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

  private final CellRepository cellRepository;

  // 첫 화면에서 셀 목록 조회
  @GetMapping("/")
  public String showCellSelectForm(Model model) {
    model.addAttribute("cells", cellRepository.findAll());
    return "select_cell";
  }

  // 선택한 셀의 출석체크 폼으로 이동

  // 출첵 폼에서 제출된 데이터를 받아 출석 정보 저장후 성공 페이지로 리다이렉트
  
  // 출켁 성공 화면

}
