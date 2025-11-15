package sf.mephy.study.orm_exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.mephy.study.orm_exam.entity.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
}
