package smmiddle.attendance.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.dto.AttendanceSummaryDto;
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
   * cellId로 셀 이름 조회
   */
  public String getCellNameById(Long cellId) {
    return cellRepository.findById(cellId)
        .map(Cell::getName)
        .orElse("존재하지 않는 셀");
  }

  /**
   * 셀별 학생들 조회
   */
  public List<Student> getAllStudentsByCellId(Long cellId) {
    return studentRepository.findByCell_IdOrderByNameAsc(cellId);
  }

  /**
   * 출석 체크 여부 확인
   */
  public boolean alreadySubmitted(Long cellId, LocalDate date) {
    return attendanceRepository.existsByStudent_Cell_IdAndDate(cellId, date);
  }

  /**
   * 오늘 특정 셀의 출석 학생 수 반환
   */
  public int getTodayPresentCount(Long cellId, LocalDate date) {
    return attendanceRepository.countByStudent_Cell_IdAndDateAndStatus(cellId, date,
        AttendanceStatus.PRESENT);
  }

  /**
   * 가장 최근 출첵한 셀
   */
  public Optional<Attendance> getLatestAttendance() {
    return attendanceRepository.findTopByUpdatedDateIsNotNullOrderByUpdatedDateDesc();
  }

  /**
   * 셀별 출석 요약정보
   */
  public AttendanceSummaryDto getAttendanceSummary(LocalDate today) {
    List<Cell> cells = getAllCells();
    Map<Long, Boolean> attendanceStatusMap = new HashMap<>();
    boolean allSubmitted = true;
    int todayPresentCount = 0;

    for (Cell cell : cells) {
      boolean submitted = alreadySubmitted(cell.getId(), today);
      attendanceStatusMap.put(cell.getId(), submitted);
      if (!submitted) {
        allSubmitted = false;
      } else {
        todayPresentCount += getTodayPresentCount(cell.getId(), today);
      }
    }

    return new AttendanceSummaryDto(attendanceStatusMap, allSubmitted, todayPresentCount);
  }

  /**
   * 출석 정보 저장
   */
  @Transactional
  public void saveAttendance(HttpServletRequest request, Long cellId, LocalDate date) {
    List<Student> students = studentRepository.findByCell_IdOrderByNameAsc(cellId);
    List<Attendance> attendances = new ArrayList<>();

    for (Student student : students) {
      String statusParam = request.getParameter("status_" + student.getId());
      String absenceReasonParam = request.getParameter("absenceReason_" + student.getId());
      String customReasonParam = request.getParameter("customReason_" + student.getId());

      AttendanceStatus status = AttendanceStatus.valueOf(statusParam);
      AbsenceReason absenceReason = null;
      String customReason = null;

      if (status == AttendanceStatus.ABSENT) {
        if (absenceReasonParam == null || absenceReasonParam.isEmpty()) {
          throw new IllegalArgumentException("결석 사유는 반드시 입력해야 합니다.");
        }
        absenceReason = AbsenceReason.valueOf(absenceReasonParam);
        if (absenceReason == AbsenceReason.OTHER) {
          customReason = customReasonParam;
        }
      }

      Attendance attendance = Attendance.builder()
          .student(student)
          .date(date)
          .status(status)
          .absenceReason(absenceReason)
          .customReason(customReason)
          .build();

      attendances.add(attendance);
    }

    attendanceRepository.saveAll(attendances);
  }

  /**
   * 출석 정보 업데이트
   */
  @Transactional
  public void updateAttendance(HttpServletRequest request, Long cellId, LocalDate date) {
    List<Student> students = studentRepository.findByCell_IdOrderByNameAsc(cellId);

    for (Student student : students) {
      String statusParam = request.getParameter("status_" + student.getId());
      String absenceReasonParam = request.getParameter("absenceReason_" + student.getId());
      String customReasonParam = request.getParameter("customReason_" + student.getId());

      AttendanceStatus status = AttendanceStatus.valueOf(statusParam);
      AbsenceReason absenceReason = null;
      String customReason = null;

      if (status == AttendanceStatus.ABSENT) {
        if (absenceReasonParam == null || absenceReasonParam.isEmpty()) {
          throw new IllegalArgumentException("결석 사유는 반드시 입력해야 합니다.");
        }
        absenceReason = AbsenceReason.valueOf(absenceReasonParam);
        if (absenceReason == AbsenceReason.OTHER) {
          customReason = customReasonParam;
        }
      }

      Attendance attendance = attendanceRepository.findByStudent_IdAndDate(student.getId(), date)
          .orElseThrow(() -> new RuntimeException("출석 정보 없음"));

      attendance.updateStatus(status, absenceReason, customReason);
    }
  }

    public List<LocalDate> getAllAttendanceDates () {
      return attendanceRepository.findDistinctDates();
    }

    // 셀 ID + 날짜로 출석 정보 가져오기
    public List<Attendance> getAttendancesByCellIdAndDate (Long cellId, LocalDate date){
      return attendanceRepository.findByStudent_Cell_IdAndDate(cellId, date);
    }

    public Map<Long, Attendance> getAttendanceMap (Long cellId, LocalDate date){
      List<Attendance> attendances = getAttendancesByCellIdAndDate(cellId, date);
      return attendances.stream()
          .collect(Collectors.toMap(attendance -> attendance.getStudent().getId(),
              attendance -> attendance));
    }

  }
