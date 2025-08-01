package smmiddle.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smmiddle.attendance.dto.RankingDto;
import smmiddle.attendance.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

  List<Student> findByCell_IdOrderByNameAsc(Long id);

  @Query("""
          SELECT new smmiddle.attendance.dto.RankingDto(
              s.name,
              s.cell.name,
              COUNT(a)
          )
          FROM Student s
          LEFT JOIN s.attendances a
            ON a.status = 'ABSENT'
           AND a.date BETWEEN :startDate AND :endDate
          GROUP BY s.id, s.name, s.cell.name, s.cell.id
          HAVING COUNT(a) BETWEEN :minAbsence AND :maxAbsence
          ORDER BY COUNT(a) ASC, s.cell.id DESC
      """)
  Page<RankingDto> findByAbsenceCountBetween(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("minAbsence") long minAbsence,
      @Param("maxAbsence") long maxAbsence,
      Pageable pageable
  );
}
