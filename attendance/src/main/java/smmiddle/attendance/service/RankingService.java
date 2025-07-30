package smmiddle.attendance.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import smmiddle.attendance.dto.RankingDto;
import smmiddle.attendance.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class RankingService {
  private final StudentRepository studentRepository;
  private static final LocalDate START_DATE = LocalDate.of(2025, 7, 1);
  private static final LocalDate END_DATE = LocalDate.of(2025, 12, 31);
  private static final int PAGE_SIZE = 10;

  public Page<RankingDto> getStudentsByAbsenceCount(int min, int max, int page) {
    Pageable pageable = PageRequest.of(page, PAGE_SIZE);

    return studentRepository.findByAbsenceCountBetween(START_DATE, END_DATE, min, max, pageable);
  }
}
