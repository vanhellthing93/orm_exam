package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.Enrollment;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.exception.InvalidRoleException;
import sf.mephy.study.orm_exam.repository.CategoryRepository;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.EnrollmentRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    public void testGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(new Course(), new Course()));

        List<Course> courses = courseService.getAllCourses();

        assertEquals(2, courses.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void testGetCourseById_Success() {
        Course course = new Course();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertNotNull(result);
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCourseById_NotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> courseService.getCourseById(1L));
    }

    @Test
    public void testGetCoursesByUserId() {
        User user = new User();
        Course course1 = new Course();
        Course course2 = new Course();
        Enrollment e1 = new Enrollment();
        Enrollment e2 = new Enrollment();
        e1.setCourse(course1);
        e2.setCourse(course2);

        when(enrollmentRepository.findByUserId(1L)).thenReturn(Arrays.asList(e1, e2));

        List<Course> courses = courseService.getCoursesByUserId(1L);

        assertEquals(2, courses.size());
        verify(enrollmentRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetStudentsByCourseId() {
        User u1 = new User();
        User u2 = new User();
        Enrollment e1 = new Enrollment();
        Enrollment e2 = new Enrollment();
        e1.setUser(u1);
        e2.setUser(u2);

        when(enrollmentRepository.findByCourseId(1L)).thenReturn(Arrays.asList(e1, e2));

        List<User> students = courseService.getStudentsByCourseId(1L);

        assertEquals(2, students.size());
        verify(enrollmentRepository, times(1)).findByCourseId(1L);
    }

    @Test
    public void testCreateCourse_Success() {
        User teacher = new User();
        teacher.setId(1L);
        teacher.setRole(User.Role.TEACHER);

        Category category = new Category();
        category.setId(2L);

        Course course = new Course();
        course.setTeacher(teacher);
        course.setCategory(category);

        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(courseRepository.save(course)).thenReturn(course);

        Course created = courseService.createCourse(course);

        assertNotNull(created);
        verify(userRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(courseRepository).save(course);
    }

    @Test
    public void testCreateCourse_InvalidTeacherRole() {
        User wrongRoleUser = new User();
        wrongRoleUser.setId(1L);
        wrongRoleUser.setRole(User.Role.STUDENT);

        Course course = new Course();
        course.setTeacher(wrongRoleUser);
        course.setCategory(new Category());

        when(userRepository.findById(1L)).thenReturn(Optional.of(wrongRoleUser));

        assertThrows(InvalidRoleException.class, () -> courseService.createCourse(course));
    }

    @Test
    public void testUpdateCourse() {
        Course existing = new Course();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setDescription("Old Description");
        User oldTeacher = new User();
        oldTeacher.setId(1L);
        oldTeacher.setRole(User.Role.TEACHER);
        existing.setTeacher(oldTeacher);
        Category oldCategory = new Category();
        oldCategory.setId(1L);
        existing.setCategory(oldCategory);

        CourseRequest request = new CourseRequest();
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setTeacherId(2L);
        request.setCategoryId(3L);
        request.setStartDate(LocalDate.now());
        request.setDuration(10);

        User newTeacher = new User();
        newTeacher.setId(2L);
        newTeacher.setRole(User.Role.TEACHER);

        Category newCategory = new Category();
        newCategory.setId(3L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newTeacher));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(newCategory));
        when(courseRepository.save(existing)).thenReturn(existing);

        Course updated = courseService.updateCourse(1L, request);

        assertEquals("New Title", updated.getTitle());
        assertEquals("New Description", updated.getDescription());
        assertEquals(newTeacher, updated.getTeacher());
        assertEquals(newCategory, updated.getCategory());
        assertEquals(request.getStartDate(), updated.getStartDate());
        assertEquals(request.getDuration(), updated.getDuration());
    }

    @Test
    public void testDeleteCourse() {
        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).delete(course);

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }
}
