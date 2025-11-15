package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.Enrollment;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.EnrollmentRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    public void testGetAllEnrollments() {
        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(new Enrollment(), new Enrollment()));

        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        assertEquals(2, enrollments.size());
        verify(enrollmentRepository).findAll();
    }

    @Test
    public void testGetEnrollmentById_Success() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment result = enrollmentService.getEnrollmentById(1L);

        assertNotNull(result);
        verify(enrollmentRepository).findById(1L);
    }

    @Test
    public void testGetEnrollmentById_NotFound() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> enrollmentService.getEnrollmentById(1L));
    }

    @Test
    public void testEnrollUserToCourse_Success() {
        User user = new User();
        user.setId(1L);
        Course course = new Course();
        course.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 2L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));

        Enrollment enrollment = enrollmentService.enrollUserToCourse(1L, 2L);

        assertNotNull(enrollment);
        assertEquals(user, enrollment.getUser());
        assertEquals(course, enrollment.getCourse());
        assertEquals(Enrollment.EnrollmentStatus.ACTIVE, enrollment.getStatus());
        assertNotNull(enrollment.getEnrollDate());

        verify(userRepository).findById(1L);
        verify(courseRepository).findById(2L);
        verify(enrollmentRepository).existsByUserIdAndCourseId(1L, 2L);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    public void testEnrollUserToCourse_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> enrollmentService.enrollUserToCourse(1L, 2L));
    }

    @Test
    public void testEnrollUserToCourse_CourseNotFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> enrollmentService.enrollUserToCourse(1L, 2L));
    }

    @Test
    public void testEnrollUserToCourse_AlreadyEnrolled() {
        User user = new User();
        user.setId(1L);
        Course course = new Course();
        course.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> enrollmentService.enrollUserToCourse(1L, 2L));
    }

    @Test
    public void testUnenrollUserFromCourse_Success() {
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 2L)).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollmentRepository).delete(enrollment);

        enrollmentService.unenrollUserFromCourse(1L, 2L);

        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    public void testUnenrollUserFromCourse_NotFound() {
        when(enrollmentRepository.findByUserIdAndCourseId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> enrollmentService.unenrollUserFromCourse(1L, 2L));
    }
}
