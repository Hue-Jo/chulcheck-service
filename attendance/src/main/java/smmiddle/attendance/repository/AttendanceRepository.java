package smmiddle.attendance.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import smmiddle.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  boolean existsByStudent_Cell_IdAndDate(Long cellId, LocalDate date);

}
