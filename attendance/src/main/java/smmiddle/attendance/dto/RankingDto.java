package smmiddle.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingDto {

  private String studentName;
  private String cellName;
  private long absenceCount;

}
