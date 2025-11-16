package sf.mephy.study.orm_exam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sf.mephy.study.orm_exam.dto.request.CourseReviewRequest;
import sf.mephy.study.orm_exam.dto.response.CourseReviewResponse;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.CourseReview;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.CourseReviewRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CourseReviewControllerIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Course course;
    private User student;
    private User teacher;

    @BeforeEach
    public void setup() {
        courseReviewRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        teacher = new User();
        teacher.setName("Teacher User");
        teacher.setEmail("teacher@email.com");
        teacher.setRole(User.Role.TEACHER);
        teacher = userRepository.save(teacher);

        course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Description");
        course.setStartDate(LocalDate.now());
        course.setDuration(10);
        course.setTeacher(teacher);
        course = courseRepository.save(course);

        student = new User();
        student.setName("Student User");
        student.setEmail("student@email.com");
        student.setRole(User.Role.STUDENT);
        student = userRepository.save(student);
    }

    @Test
    public void testCreateCourseReview() throws Exception {
        CourseReviewRequest request = new CourseReviewRequest();
        request.setCourseId(course.getId());
        request.setComment("Great course!");
        request.setRating(5);
        request.setStudentId(student.getId());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/course-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Great course!"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    public void testCreateCourseReviewWithPath() throws Exception {
        CourseReviewRequest request = new CourseReviewRequest();
        request.setComment("Excellent!");
        request.setRating(4);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/course-reviews/{courseId}/{studentId}", course.getId(), student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Excellent!"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    public void testGetCourseReviewById() throws Exception {
        CourseReview review = new CourseReview();
        review.setComment("Informative");
        review.setRating(5);
        review.setCourse(course);
        review.setStudent(student);
        review = courseReviewRepository.save(review);

        mockMvc.perform(get("/api/v1/course-reviews/{id}", review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Informative"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    public void testGetAllCourseReviews() throws Exception {
        CourseReview review1 = new CourseReview();
        review1.setComment("Review 1");
        review1.setRating(3);
        review1.setCourse(course);
        review1.setStudent(student);

        CourseReview review2 = new CourseReview();
        review2.setComment("Review 2");
        review2.setRating(4);
        review2.setCourse(course);
        review2.setStudent(student);

        courseReviewRepository.save(review1);
        courseReviewRepository.save(review2);

        mockMvc.perform(get("/api/v1/course-reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void testUpdateCourseReview() throws Exception {
        CourseReview review = new CourseReview();
        review.setComment("Old text");
        review.setRating(2);
        review.setCourse(course);
        review.setStudent(student);
        review = courseReviewRepository.save(review);

        CourseReviewRequest request = new CourseReviewRequest();
        request.setComment("Updated text");
        request.setRating(5);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/course-reviews/{id}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated text"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    public void testDeleteCourseReview() throws Exception {
        CourseReview review = new CourseReview();
        review.setComment("To be deleted");
        review.setRating(1);
        review.setCourse(course);
        review.setStudent(student);
        review = courseReviewRepository.save(review);

        mockMvc.perform(delete("/api/v1/course-reviews/{id}", review.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/course-reviews/{id}", review.getId()))
                .andExpect(status().isNotFound());
    }
}
