package smmiddle.attendance.component;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {

  private SessionUtil() {}

  public static boolean isNotAuthenticated(HttpSession session) {
    Boolean authenticated = (Boolean) session.getAttribute("authenticated");
    return authenticated == null || !authenticated;
  }
}
