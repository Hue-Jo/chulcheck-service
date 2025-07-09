package smmiddle.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smmiddle.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  boolean existsByStudent_Cell_IdAndDate(Long cellId, LocalDate date);

  List<Attendance> findByStudent_Cell_IdAndDate(Long cellId, LocalDate date);

  // 출석 날짜 목록 조회 (중복 제거)
  @Query("SELECT DISTINCT a.date FROM Attendance a ORDER BY a.date DESC")
  List<LocalDate> findDistinctDates();


  List<Attendance> findByDate(LocalDate date);
}
