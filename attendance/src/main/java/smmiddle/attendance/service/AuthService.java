package smmiddle.attendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  @Value("${auth.code}")
  private String correctCode;

  public boolean verifyCode(String inputCode) {
    return correctCode.equals(inputCode);
  }

}
