package smmiddle.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import smmiddle.attendance.constant.AttendanceStatus;

@Entity
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private AttendanceStatus status;

  private String absenceReason;

  @ManyToOne
  private Student student;


}
