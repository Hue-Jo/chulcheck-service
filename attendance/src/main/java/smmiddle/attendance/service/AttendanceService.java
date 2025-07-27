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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smmiddle.attendance.constant.AbsenceReason;
import smmiddle.attendance.constant.AttendanceStatus;
import smmiddle.attendance.dto.AllAttendanceSummaryDto;
import smmiddle.attendance.dto.CellAttendanceSummaryDto;
import smmiddle.attendance.entity.Attendance;
import smmiddle.attendance.entity.Cell;
import smmiddle.attendance.entity.Student;
import smmiddle.attendance.exception.ChulCheckException;
import smmiddle.attendance.exception.ErrorCode;
import smmiddle.attendance.repository.AttendanceRepository;
import smmiddle.attendance.repository.CellRepository;
import smmiddle.attendance.repository.StudentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final StudentRepository studentRepository;
  private final CellRepository cellRepository;

  private static final String STATUS_PREFIX = "status_";
  private static final String ABSENCE_REASON_PREFIX = "absenceReason_";
  private static final String CUSTOM_REASON_PREFIX = "customReason_";

  /**
   * 모든 셀 조회
   */
  public List<Cell> getAllCells() {
    log.debug("모든 셀 정보 조회 요청");
    return cellRepository.findAll();
  }

  /**
   * 특정 셀 조회
   */
  public Cell getCellById(Long cellId) {
    if (cellId == null || cellId <= 0) {
      log.warn("잘못된 셀 ID: {}", cellId);
      throw new ChulCheckException(ErrorCode.INVALID_CELL_ID);
    }

    return cellRepository.findById(cellId)
        .orElseThrow(() -> {
          log.warn("셀 ID [{}]에 해당하는 셀이 존재하지 않습니다.", cellId);
          return new ChulCheckException(ErrorCode.CELL_NOT_FOUND);
        });
  }

  /**
   * cellId로 셀 이름 조회
   */
  public String getCellNameById(Long cellId) {
    if (cellId == null || cellId <= 0) {
      log.warn("잘못된 셀 ID: {}", cellId);
      throw new ChulCheckException(ErrorCode.INVALID_CELL_ID);
    }

    return cellRepository.findById(cellId)
        .map(Cell::getName)
        .orElseThrow(() -> {
          log.warn("셀 ID [{}]에 해당하는 셀이 존재하지 않습니다.", cellId);
          throw new ChulCheckException(ErrorCode.CELL_NOT_FOUND);
        });
  }

  /**
   * 셀별 학생들 조회
   */
  public List<Student> getAllStudentsByCellId(Long cellId) {
    if (cellId == null || cellId <= 0) {
      log.warn("잘못된 셀 ID: {}", cellId);
      throw new ChulCheckException(ErrorCode.INVALID_CELL_ID);
    }

    if (!cellRepository.existsById(cellId)) {
      log.warn("셀 ID [{}]에 해당하는 셀이 존재하지 않습니다.", cellId);
      throw new ChulCheckException(ErrorCode.CELL_NOT_FOUND);
    }

    log.debug("셀 ID [{}]에 속한 학생들 조회", cellId);
    return studentRepository.findByCell_IdOrderByNameAsc(cellId);
  }

  /**
   * 출석 체크 여부 확인
   */
  public boolean alreadySubmitted(Long cellId, LocalDate date) {
    return attendanceRepository.existsByStudent_Cell_IdAndDate(cellId, date);
  }

  /**
   * 출석 수 계산 메서드
   */
  public boolean countsAsPresent(Attendance att) {
    return att.getStatus() == AttendanceStatus.PRESENT
        || att.getStatus() == AttendanceStatus.ALLOWED;

  }

  /**
   * 오늘 특정 셀의 출석 학생 수 반환
   */
  public int getTodayPresentCount(Long cellId, LocalDate date) {
    List<Attendance> attendances = attendanceRepository.findByStudent_Cell_IdAndDateOrderByStudent_NameAsc(cellId, date);

    int count = (int) attendances.stream()
        .filter(this::countsAsPresent)
        .count();

    log.debug("셀 ID [{}], 날짜 [{}]의 출석 인원 수: {}", cellId, date, count);
    return count;
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
  public AllAttendanceSummaryDto getAttendanceSummary(LocalDate today) {
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

    return new AllAttendanceSummaryDto(attendanceStatusMap, allSubmitted, todayPresentCount);
  }

  /**
   * 출석정보 제출/수정
   */
  @Transactional
  public String submitAttendance(HttpServletRequest request, Long cellId, LocalDate date) {
    boolean hasExisting = false;

    List<Student> students = studentRepository.findByCell_IdOrderByNameAsc(cellId);

    for (Student student : students) {
      String statusParam = request.getParameter(STATUS_PREFIX + student.getId());
      String absenceReasonParam = request.getParameter(ABSENCE_REASON_PREFIX + student.getId());
      String customReasonParam = request.getParameter(CUSTOM_REASON_PREFIX + student.getId());

      AttendanceStatus status = AttendanceStatus.valueOf(statusParam);
      AbsenceReason absenceReason = null;
      String customReason = null;

      if (status == AttendanceStatus.ABSENT) {
        if (absenceReasonParam == null || absenceReasonParam.isEmpty()) {
          throw new ChulCheckException(ErrorCode.INVALID_ABSENCE_REASON);
        }
        absenceReason = AbsenceReason.valueOf(absenceReasonParam);
        if (absenceReason == AbsenceReason.OTHER) {
          customReason = customReasonParam;
        }

        // ALLOWED 상태로 변경 조건
        if (absenceReason == AbsenceReason.NAVE || absenceReason == AbsenceReason.OTHER_CHURCH) {
          status = AttendanceStatus.ALLOWED;
        }
      }

      Optional<Attendance> attendanceOpt = attendanceRepository.findByStudent_IdAndDate(student.getId(), date);

      if (attendanceOpt.isPresent()) {
        attendanceOpt.get().updateStatus(status, absenceReason, customReason);
        hasExisting = true;
      } else {
        Attendance attendance = Attendance.builder()
            .student(student)
            .date(date)
            .status(status)
            .absenceReason(absenceReason)
            .customReason(customReason)
            .build();

        attendanceRepository.save(attendance);
      }
    }
    log.info("출석 정보 {} 완료 - 셀 ID: {}, 날짜: {}", hasExisting ? "수정" : "제출", cellId, date);
    return hasExisting ? "수정" : "제출";
  }

  public List<LocalDate> getAllAttendanceDates() {
    return attendanceRepository.findDistinctDates();
  }

  // 셀 ID + 날짜로 출석 정보 가져오기
  public List<Attendance> getAttendancesByCellIdAndDate(Long cellId, LocalDate date) {
    log.debug("셀 ID [{}], 날짜 [{}]의 출석 정보 조회", cellId, date);
    return attendanceRepository.findByStudent_Cell_IdAndDateOrderByStudent_NameAsc(cellId, date);
  }

  public Map<Long, Attendance> getAttendanceMap(Long cellId, LocalDate date) {
    List<Attendance> attendances = getAttendancesByCellIdAndDate(cellId, date);
    return attendances.stream()
        .collect(Collectors.toMap(
            attendance -> attendance.getStudent().getId(),
            attendance -> attendance));
  }

  public List<CellAttendanceSummaryDto> getTodayCellAttendanceSummary() {
    LocalDate today = LocalDate.now();
    List<Cell> cells = cellRepository.findAll();
    List<CellAttendanceSummaryDto> summaries = new ArrayList<>();

    for (Cell cell : cells) {
      boolean submitted = attendanceRepository.existsByStudent_Cell_IdAndDate(cell.getId(), today);

      String presentCountDisplay;
      if (submitted) {
        List<Attendance> attendances = attendanceRepository.findByStudent_Cell_IdAndDateOrderByStudent_NameAsc(cell.getId(), today);
        long presentCount = attendances.stream()
            .filter(this::countsAsPresent)
            .count();
        presentCountDisplay = presentCount + "명";
      } else {
        presentCountDisplay = "⌛";
      }

      summaries.add(new CellAttendanceSummaryDto(cell.getName(), submitted, presentCountDisplay));
    }

    return summaries;
  }

}
