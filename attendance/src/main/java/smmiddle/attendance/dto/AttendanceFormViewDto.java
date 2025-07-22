package smmiddle.attendance.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;

@Getter
@Builder
public class AttendanceFormViewDto {
  private Cell cell;
  private List<Student> students;
  private LocalDate today;
  private Map<Long, Attendance> attendanceMap;
  private boolean isEdit;
  private List<AbsenceReason> absenceReasons;

}
