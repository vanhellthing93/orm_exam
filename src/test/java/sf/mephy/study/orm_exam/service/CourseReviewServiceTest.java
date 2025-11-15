package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.CourseReviewRequest;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.CourseReview;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.CourseReviewRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseReviewServiceTest {

    @Mock
    private CourseReviewRepository courseReviewRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseReviewService courseReviewService;

    @Test
    public void testGetAllCourseReviews() {
        when(courseReviewRepository.findAll()).thenReturn(Arrays.asList(new CourseReview(), new CourseReview()));

        List<CourseReview> reviews = courseReviewService.getAllCourseReviews();

        assertEquals(2, reviews.size());
        verify(courseReviewRepository).findAll();
    }

    @Test
    public void testGetCourseReviewById_Success() {
        CourseReview review = new CourseReview();
        when(courseReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        CourseReview result = courseReviewService.getCourseReviewById(1L);

        assertNotNull(result);
        verify(courseReviewRepository).findById(1L);
    }

    @Test
    public void testGetCourseReviewById_NotFound() {
        when(courseReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.getCourseReviewById(1L));
    }

    @Test
    public void testCreateCourseReview_Success() {
        Course course = new Course();
        course.setId(1L);
        User student = new User();
        student.setId(2L);

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(courseReviewRepository.save(any(CourseReview.class))).thenAnswer(i -> i.getArgument(0));

        CourseReview created = courseReviewService.createCourseReview(review);

        assertNotNull(created);
        assertEquals(course, created.getCourse());
        assertEquals(student, created.getStudent());
        verify(courseRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(courseReviewRepository).save(review);
    }

    @Test
    public void testCreateCourseReview_CourseNotFound() {
        CourseReview review = new CourseReview();
        Course course = new Course();
        course.setId(1L);
        User student = new User();
        student.setId(2L);
        review.setCourse(course);
        review.setStudent(student);

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.createCourseReview(review));
    }

    @Test
    public void testCreateCourseReview_StudentNotFound() {
        CourseReview review = new CourseReview();
        Course course = new Course();
        course.setId(1L);
        User student = new User();
        student.setId(2L);
        review.setCourse(course);
        review.setStudent(student);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.createCourseReview(review));
    }

    @Test
    public void testUpdateCourseReview_Success() {
        CourseReview existing = new CourseReview();
        existing.setRating(3);
        existing.setComment("Old comment");
        Course oldCourse = new Course();
        oldCourse.setId(1L);
        existing.setCourse(oldCourse);
        User oldStudent = new User();
        oldStudent.setId(1L);
        existing.setStudent(oldStudent);

        CourseReviewRequest request = new CourseReviewRequest();
        request.setRating(5);
        request.setComment("New comment");
        request.setCourseId(2L);
        request.setStudentId(3L);

        Course newCourse = new Course();
        newCourse.setId(2L);
        User newStudent = new User();
        newStudent.setId(3L);

        when(courseReviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(newCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newStudent));
        when(courseReviewRepository.save(existing)).thenReturn(existing);

        CourseReview updated = courseReviewService.updateCourseReview(1L, request);

        assertEquals(5, updated.getRating());
        assertEquals("New comment", updated.getComment());
        assertEquals(newCourse, updated.getCourse());
        assertEquals(newStudent, updated.getStudent());
    }

    @Test
    public void testUpdateCourseReview_NotFound() {
        CourseReviewRequest request = new CourseReviewRequest();
        when(courseReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.updateCourseReview(1L, request));
    }

    @Test
    public void testUpdateCourseReview_CourseNotFound() {
        CourseReview existing = new CourseReview();
        CourseReviewRequest request = new CourseReviewRequest();
        request.setCourseId(2L);

        when(courseReviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.updateCourseReview(1L, request));
    }

    @Test
    public void testUpdateCourseReview_StudentNotFound() {
        CourseReview existing = new CourseReview();
        CourseReviewRequest request = new CourseReviewRequest();
        request.setStudentId(3L);

        when(courseReviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.updateCourseReview(1L, request));
    }

    @Test
    public void testDeleteCourseReview_Success() {
        CourseReview review = new CourseReview();
        when(courseReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(courseReviewRepository).delete(review);

        courseReviewService.deleteCourseReview(1L);

        verify(courseReviewRepository).delete(review);
    }

    @Test
    public void testDeleteCourseReview_NotFound() {
        when(courseReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseReviewService.deleteCourseReview(1L));
    }
}
