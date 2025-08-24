package smmiddle.attendance.dto;

import lombok.Builder;
import lombok.Getter;
import smmiddle.attendance.entity.Student;

@Getter
@Builder
public class StudentDto {
  private Long id;
  private String name;
  private String cellName;

  // Student 엔티티를 DTO로 변환
  public static StudentDto fromEntity(Student student) {
    return StudentDto.builder()
        .id(student.getId())
        .name(student.getName())
        .cellName(student.getCell().getName())
        .build();
  }
}
