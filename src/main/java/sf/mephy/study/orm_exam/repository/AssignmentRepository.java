package sf.mephy.study.orm_exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.mephy.study.orm_exam.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
