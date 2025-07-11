package smmiddle.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smmiddle.attendance.constant.AttendanceStatus;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private AttendanceStatus status;

  private String reason;

  @ManyToOne
  private Student student;


  public void updateStatus(AttendanceStatus status, String reason) {
    this.status = status;
    this.reason = (status == AttendanceStatus.ABSENT) ? reason : null;
  }
}
