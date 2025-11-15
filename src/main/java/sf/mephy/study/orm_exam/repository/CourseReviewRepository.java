package sf.mephy.study.orm_exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.mephy.study.orm_exam.entity.CourseReview;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
}
