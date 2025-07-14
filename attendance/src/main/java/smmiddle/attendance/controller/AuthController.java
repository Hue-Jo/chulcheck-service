package smmiddle.attendance.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

  @Value("${auth.code}")
  private String correctCode;

  // GET: 인증번호 입력 폼 보여주기
  @GetMapping("/auth")
  public String showAuthForm(Model model) {
    return "auth"; // 인증번호 입력 화면
  }

  // POST: 인증번호 확인
  @PostMapping("/auth")
  public String verifyCode(
      @RequestParam String code,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    if (correctCode.equals(code)) {
      session.setAttribute("authenticated", true); // 세션에 인증 플래그 저장
      return "redirect:/"; // 인증 성공 시 첫 화면으로 이동
    } else {
      redirectAttributes.addFlashAttribute("error", "❌ 인증번호가 올바르지 않습니다.");
      return "redirect:/auth"; // 실패 시 다시 인증 페이지로
    }
  }
}
