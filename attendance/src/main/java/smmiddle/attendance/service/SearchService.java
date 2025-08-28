package smmiddle.attendance.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smmiddle.attendance.dto.StudentAttendanceDto;
import smmiddle.attendance.dto.StudentDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.repository.AttendanceRepository;
import smmiddle.attendance.repository.StudentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final StudentRepository studentRepository;
  private final AttendanceRepository attendanceRepository;

  /**
   * 이름으로 학생 검색
   */
  public List<StudentDto> searchStudentByName(String name) {
    log.info("{} 학생 이름 검색", name);
    return studentRepository.findByName(name).stream()
        .map(StudentDto::fromEntity)
        .toList();
  }

  /**
   * 특정 학생의 기간 내 출석 내역 조회
   */
  public List<StudentAttendanceDto> getEachStudentsAttendance(Long studentId) {
    LocalDate startDate = LocalDate.now().withDayOfYear(1);
    LocalDate endDate = LocalDate.now();
    return attendanceRepository.findByStudent_IdAndDateBetweenOrderByDateDesc(studentId, startDate, endDate)
        .stream()
        .map(StudentAttendanceDto::fromEntity)
        .toList();

  }

}
