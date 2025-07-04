package smmiddle.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smmiddle.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


}
