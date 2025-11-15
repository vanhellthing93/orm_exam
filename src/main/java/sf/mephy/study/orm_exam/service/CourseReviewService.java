package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.CourseReviewRequest;
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
        Long courseId = courseReview.getCourse().getId();
        Long studentId = courseReview.getStudent().getId();

        return createCourseReviewWithCourseIdAndStudentId(courseReview, courseId, studentId);
    }
    public CourseReview createCourseReview(Long courseId, Long studentId, CourseReview courseReviewDetails) {
        return createCourseReviewWithCourseIdAndStudentId(courseReviewDetails, courseId, studentId);
    }
    private CourseReview createCourseReviewWithCourseIdAndStudentId(CourseReview courseReview, Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with id " + courseId + " not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + studentId + " not found"));

        courseReview.setCourse(course);
        courseReview.setStudent(student);

        return courseReviewRepository.save(courseReview);
    }

    public CourseReview updateCourseReview(Long id, CourseReviewRequest courseReviewRequest) {
        CourseReview existingReview = courseReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseReview with id " + id + " not found"));

        if (courseReviewRequest.getRating() != null) {
            existingReview.setRating(courseReviewRequest.getRating());
        }

        if (courseReviewRequest.getComment() != null) {
            existingReview.setComment(courseReviewRequest.getComment());
        }

        if (courseReviewRequest.getStudentId() != null) {
            User student = userRepository.findById(courseReviewRequest.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + courseReviewRequest.getStudentId() + " not found"));
            existingReview.setStudent(student);
        }

        if (courseReviewRequest.getCourseId() != null) {
            Course course = courseRepository.findById(courseReviewRequest.getCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course with id " + courseReviewRequest.getCourseId() + " not found"));
            existingReview.setCourse(course);
        }

        return courseReviewRepository.save(existingReview);
    }

    public void deleteCourseReview(Long id) {
        CourseReview review = courseReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CourseReview with id " + id + " not found"));
        courseReviewRepository.delete(review);
    }
}
