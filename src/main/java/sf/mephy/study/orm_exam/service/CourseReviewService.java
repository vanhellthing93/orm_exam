package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.CourseReview;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.CourseReviewRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseReviewService {
    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<CourseReview> getAllCourseReviews() {
        return courseReviewRepository.findAll();
    }

    public CourseReview getCourseReviewById(Long id) {
        return courseReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseReview with id " + id + " not found"));
    }

    public CourseReview createCourseReview(CourseReview courseReview) {
        return courseReviewRepository.save(courseReview);
    }

    public CourseReview createCourseReview(Long courseId, Long studentId, CourseReview courseReviewDetails) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with id " + courseId + " not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + studentId + " not found"));

        courseReviewDetails.setCourse(course);
        courseReviewDetails.setStudent(student);

        return courseReviewRepository.save(courseReviewDetails);
    }
}
