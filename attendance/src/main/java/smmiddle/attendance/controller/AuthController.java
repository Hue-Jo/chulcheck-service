package smmiddle.attendance.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import smmiddle.attendance.service.AuthService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  // 인증번호 입력 폼 보여주기
  @GetMapping("/auth")
  public String showAuthForm(Model model) {
    log.info("인증번호 입력 폼 요청");
    return "auth"; // 인증번호 입력 화면
  }

  // 인증번호 확인
  @PostMapping("/auth")
  public String verifyCode(
      @RequestParam String code,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    log.info("입력된 인증번호: {}", code);

    if (authService.verifyCode(code)) {
      session.setAttribute("authenticated", true); // 세션에 인증 플래그 저장
      return "redirect:/"; // 인증 성공 시 첫 화면으로 이동

    } else {
      log.warn("인증 실패 - 잘못된 코드 입력: {}", code);
      redirectAttributes.addFlashAttribute("error", "❌ 인증번호가 올바르지 않습니다.");
      return "redirect:/auth"; // 실패 시 다시 인증 페이지로
    }
  }
}
