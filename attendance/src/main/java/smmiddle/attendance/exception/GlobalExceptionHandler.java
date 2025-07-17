package smmiddle.attendance.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ChulCheckException.class)
  public String handleChulCheckException(ChulCheckException e, RedirectAttributes redirectAttributes) {

//    ErrorCode code = e.getErrorCode();
//
//    model.addAttribute("errorMessage", code.getMessage());
//
//    return "redirect:/";
    redirectAttributes.addFlashAttribute("chulcheckErrorMessage", e.getErrorCode().getMessage());
    return "redirect:/";

  }

}
