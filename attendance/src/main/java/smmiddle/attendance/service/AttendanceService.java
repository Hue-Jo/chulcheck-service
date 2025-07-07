package smmiddle.attendance.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
   * 출석 정보 저장
   */
  @Transactional
  public void saveAttendance() {

  }





}
