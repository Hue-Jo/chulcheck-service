package smmiddle.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smmiddle.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  boolean existsByStudent_Cell_IdAndDate(Long cellId, LocalDate date);

  List<Attendance> findByStudent_Cell_IdAndDateOrderByStudent_NameAsc(Long cellId, LocalDate date);

  Optional<Attendance> findByStudent_IdAndDate(Long studentId, LocalDate date);

  // 출석 날짜 목록 조회 (중복 제거)
  @Query("SELECT DISTINCT a.date FROM Attendance a ORDER BY a.date DESC")
  List<LocalDate> findDistinctDates();

  Optional<Attendance> findTopByUpdatedDateIsNotNullOrderByUpdatedDateDesc();

  Optional<Attendance> findTopByStudent_IdOrderByDateDesc(Long studentId);

  List<Attendance> findByStudent_IdAndDateBetweenOrderByDateDesc(Long studentId, LocalDate start, LocalDate end);

}
