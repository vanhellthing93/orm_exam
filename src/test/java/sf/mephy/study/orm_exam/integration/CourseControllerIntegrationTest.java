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
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.dto.response.CourseResponse;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.repository.CategoryRepository;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CourseControllerIntegrationTest {

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
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User teacher;
    private Category category;
    private CourseRequest baseCourseRequest;

    @BeforeEach
    public void setup() {
        // Создаем учителя и категорию, используемые во всех тестах
        teacher = new User();
        teacher.setName("Integration Teacher");
        teacher.setEmail("integration.teacher@mail.com");
        teacher.setRole(User.Role.TEACHER);
        teacher = userRepository.save(teacher);

        category = new Category();
        category.setName("Integration");
        category = categoryRepository.save(category);

        baseCourseRequest = new CourseRequest();
        baseCourseRequest.setTeacherId(teacher.getId());
        baseCourseRequest.setCategoryId(category.getId());
        baseCourseRequest.setStartDate(LocalDate.of(2025, 11, 1));
        baseCourseRequest.setDuration(30);
    }

    @Test
    public void testCreateCourse() throws Exception {
        baseCourseRequest.setTitle("Integration Course");
        baseCourseRequest.setDescription("Course Description");
        String json = objectMapper.writeValueAsString(baseCourseRequest);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Course"))
                .andExpect(jsonPath("$.description").value("Course Description"))
                .andExpect(jsonPath("$.teacher.id").value(teacher.getId()));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        // Сначала создаем курс
        baseCourseRequest.setTitle("Initial Title");
        baseCourseRequest.setDescription("Initial Description");
        CourseResponse created = createCourse(baseCourseRequest);

        // Обновляем курс
        baseCourseRequest.setTitle("Updated Title");
        baseCourseRequest.setDescription("Updated Description");
        String json = objectMapper.writeValueAsString(baseCourseRequest);

        mockMvc.perform(put("/api/v1/courses/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    public void testGetCourseById() throws Exception {
        baseCourseRequest.setTitle("Course to Get");
        baseCourseRequest.setDescription("Description");
        CourseResponse created = createCourse(baseCourseRequest);

        mockMvc.perform(get("/api/v1/courses/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Course to Get"));
    }

    @Test
    public void testGetAllCourses() throws Exception {
        // Убедимся, что есть хотя бы один курс
        baseCourseRequest.setTitle("Course List");
        baseCourseRequest.setDescription("Description");
        createCourse(baseCourseRequest);

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    public void testDeleteCourse() throws Exception {
        baseCourseRequest.setTitle("Course to Delete");
        baseCourseRequest.setDescription("Description");
        CourseResponse created = createCourse(baseCourseRequest);

        mockMvc.perform(delete("/api/v1/courses/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/courses/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStudentsByCourseId_Empty() throws Exception {
        baseCourseRequest.setTitle("Course with Students");
        baseCourseRequest.setDescription("Description");
        CourseResponse created = createCourse(baseCourseRequest);

        mockMvc.perform(get("/api/v1/courses/{courseId}/students", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    // Утилитарный метод для создания курса через POST и возврата десериализованного ответа
    private CourseResponse createCourse(CourseRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);
        String responseJson = mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(responseJson, CourseResponse.class);
    }
}
