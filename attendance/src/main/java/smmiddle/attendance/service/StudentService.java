package smmiddle.attendance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.exception.ChulCheckException;
import smmiddle.attendance.exception.ErrorCode;
import smmiddle.attendance.repository.CellRepository;
import smmiddle.attendance.repository.StudentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {
  private final StudentRepository studentRepository;
  private final CellRepository cellRepository;

  /**
   * 새 학생 추가
   */
  @Transactional
  public void addNewStudent(Long cellId, String newStudentName) {
    Cell cell = cellRepository.findById(cellId)
        .orElseThrow(() -> new ChulCheckException(ErrorCode.CELL_NOT_FOUND));

    Student student = Student.builder()
        .name(newStudentName)
        .cell(cell)
        .build();

    studentRepository.save(student);
    log.info("새 친구 저장 완료: 이름 = {}, 셀 = {}", student.getName(), cell.getName());
  }

}
