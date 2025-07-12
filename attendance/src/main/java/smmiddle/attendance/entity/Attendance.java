package smmiddle.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import smmiddle.attendance.constant.AttendanceStatus;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  private LocalDate date;

  private LocalDateTime updatedDate;

  @Enumerated(EnumType.STRING)
  private AttendanceStatus status;

  private String reason;

  @ManyToOne
  private Student student;


  public void updateStatus(AttendanceStatus status, String reason) {
    this.status = status;
    this.reason = (status == AttendanceStatus.ABSENT) ? reason : null;
    this.updatedDate = LocalDateTime.now();
  }
}
