package sf.mephy.study.orm_exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sf.mephy.study.orm_exam.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
