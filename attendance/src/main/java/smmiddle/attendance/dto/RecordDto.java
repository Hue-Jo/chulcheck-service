package smmiddle.attendance.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import smmiddle.attendance.entity.Attendance;

@Getter
@AllArgsConstructor
public class RecordDto {
  private List<Attendance> attendanceList;
  private long presentCount;

}
