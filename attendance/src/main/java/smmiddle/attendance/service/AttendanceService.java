package smmiddle.attendance.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.dto.AttendanceResponse;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.repository.AttendanceRepository;
import smmiddle.attendance.repository.CellRepository;
import smmiddle.attendance.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final StudentRepository studentRepository;
  private final CellRepository cellRepository;

  /**
   * 모든 셀 조회
   */
  public List<Cell> getAllCells() {
    return cellRepository.findAll();
  }

  /**
   * 특정 셀 조회
   */
  public Cell getCellById(Long cellId) {
    return cellRepository.findById(cellId)
        .orElseThrow(() -> new IllegalArgumentException("해당 셀이 존재하지 않습니다."));
  }

  /**
   * 셀별 학생들 조회
   */
  public List<Student> getAllStudentsByCellId(Long cellId) {
    return studentRepository.findByCell_Id(cellId);
  }

  /**
   * 출석 체크 여부 확인
   */
  public boolean alreadySubmitted(Long cellId, LocalDate date) {
    return attendanceRepository.existsByStudent_Cell_IdAndDate(cellId, date);
  }

  /**
   * 출석 정보 저장
   */
  @Transactional
  public void saveAttendance(HttpServletRequest request, Long cellId, LocalDate date) {
    List<Student> students = studentRepository.findByCell_Id(cellId);
    List<Attendance> attendances = new ArrayList<>();

    for (Student student : students) {
      String statusParam = request.getParameter("status_" + student.getId());
      String reasonParam = request.getParameter("reason_" + student.getId());

      AttendanceStatus status = AttendanceStatus.valueOf(statusParam);
      Attendance attendance = Attendance.builder()
          .student(student)
          .date(date)
          .status(status)
          .reason(status == AttendanceStatus.ABSENT ? reasonParam : null)
          .build();
      attendances.add(attendance);
    }
    attendanceRepository.saveAll(attendances);
  }


  public List<LocalDate> getAllAttendanceDates() {
    return  attendanceRepository.findDistinctDates();
  }

  // 셀 ID + 날짜로 출석 정보 가져오기
  public List<Attendance> getAttendancesByCellIdAndDate(Long cellId, LocalDate date) {
    return attendanceRepository.findByStudent_Cell_IdAndDate(cellId, date);
  }

}
