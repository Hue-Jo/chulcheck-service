package smmiddle.attendance;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smmiddle.attendance.constant.AttendanceStatus;
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
   * 셀별 학생들 조회
   */
  public List<Student> getAllStudentsByCell(Long cellId) {
    Cell cell = cellRepository.findById(cellId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 셀입니다."));
    return studentRepository.findByCell(cell);
  }

  /**
   * 출석 정보 저장
   */
  @Transactional
  public void saveAttendance() {

  }





}
