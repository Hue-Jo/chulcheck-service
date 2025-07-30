package smmiddle.attendance.controller;

import static smmiddle.attendance.component.SessionUtil.isNotAuthenticated;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smmiddle.attendance.dto.RankingDto;
import smmiddle.attendance.service.RankingService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RankingController {

  private final RankingService rankingService;

  @GetMapping("/ranking")
  public String getRanking(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(required = false) Integer min,
      @RequestParam(required = false) Integer max,
      Model model,
      HttpSession session
  ) {

    if (isNotAuthenticated(session)) {
      log.warn("비인증 사용자가 접근 시도");
      return "redirect:/auth";  // 인증 안 됐으면 인증 페이지로 보내기
    }

    // 기본값: 결석 0회
    int minAbsence = (min != null) ? min : 0;
    int maxAbsence = (max != null) ? max : 0;

    Page<RankingDto> result = rankingService.getStudentsByAbsenceCount(minAbsence, maxAbsence, page);

    model.addAttribute("rankingList", result.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", result.getTotalPages());
    model.addAttribute("min", minAbsence);
    model.addAttribute("max", maxAbsence);
    return "ranking";
  }
}

