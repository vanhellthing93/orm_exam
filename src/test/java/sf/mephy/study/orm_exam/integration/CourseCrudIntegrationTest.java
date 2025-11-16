package sf.mephy.study.orm_exam.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class CourseCrudIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName(System.getenv().getOrDefault("DB_NAME", "testdb"))
            .withUsername(System.getenv().getOrDefault("DB_USERNAME", "testuser"))
            .withPassword(System.getenv().getOrDefault("DB_PASSWORD", "testpass"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindCourse() {
        // Создаём и сохраняем учителя
        User teacher = new User();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@mail.ru");
        teacher.setRole(User.Role.TEACHER);
        teacher = userRepository.save(teacher);

        // Создаём курс и задаём обязательное поле teacher
        Course course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setTeacher(teacher);

        Course savedCourse = courseRepository.save(course);
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElse(null);

        assertNotNull(foundCourse);
        assertEquals("Test Course", foundCourse.getTitle());
        assertEquals("Test Description", foundCourse.getDescription());
    }
}
