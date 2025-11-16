package sf.mephy.study.orm_exam.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sf.mephy.study.orm_exam.entity.*;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.repository.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class CourseCascadeDeleteIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCascadeDeleteCourse() {
        // Создаем и сохраняем сущности
        User teacher = new User();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@example.com");
        teacher.setRole(User.Role.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);
        course = courseRepository.findById(course.getId()).orElse(null);

        Module module = new Module();
        module.setTitle("Test Module");
        module.setCourse(course);
        moduleRepository.save(module);

        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setModule(module);
        lessonRepository.save(lesson);

        Assignment assignment = new Assignment();
        assignment.setTitle("Test Assignment");
        assignment.setLesson(lesson);
        assignmentRepository.save(assignment);

        Quiz quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        quiz.setModule(module);
        quizRepository.save(quiz);

        // Проверяем, что сущности сохранены
        assertNotNull(course.getId());
        assertNotNull(module.getId());
        assertNotNull(lesson.getId());
        assertNotNull(assignment.getId());
        assertNotNull(quiz.getId());

        // Удаляем курс
        courseRepository.delete(course);

        // Проверяем, что связанные сущности удалены каскадно
        assertFalse(moduleRepository.existsById(module.getId()));
        assertFalse(lessonRepository.existsById(lesson.getId()));
        assertFalse(assignmentRepository.existsById(assignment.getId()));
        assertFalse(quizRepository.existsById(quiz.getId()));
    }
}
