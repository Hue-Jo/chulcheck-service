package smmiddle.attendance.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ChulCheckException.class)
  public String handleChulCheckException(ChulCheckException e, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("chulcheckErrorMessage", e.getErrorCode().getMessage());
    return "redirect:/";

  }

}
