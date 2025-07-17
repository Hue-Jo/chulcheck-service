package smmiddle.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CellAttendanceSummaryDto {
  private final String cellName;
  private final boolean submitted;
  private final String presentCount; // '11명' , '⌛'

}
