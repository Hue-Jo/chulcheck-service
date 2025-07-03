package smmiddle.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smmiddle.attendance.entity.Cell;

public interface CellRepository extends JpaRepository<Cell, Integer> {

}
