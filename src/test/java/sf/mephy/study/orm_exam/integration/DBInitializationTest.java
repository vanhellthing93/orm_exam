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
import sf.mephy.study.orm_exam.entity.*;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.repository.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class DBInitializationTest {

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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    public void testDatabaseInitializationAndCrud() {
        // 1. Проверка существования записей, которые должны были быть созданы Liquibase

        // Категории
        List<Category> categories = categoryRepository.findAll();
        assertFalse(categories.isEmpty(), "Categories should be preloaded");

        // Пользователи
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty(), "Users should be preloaded");

        // Курсы
        List<Course> courses = courseRepository.findAll();
        assertFalse(courses.isEmpty(), "Courses should be preloaded");

        // Модули
        List<Module> modules = moduleRepository.findAll();
        assertFalse(modules.isEmpty(), "Modules should be preloaded");

        // Уроки
        List<Lesson> lessons = lessonRepository.findAll();
        assertFalse(lessons.isEmpty(), "Lessons should be preloaded");

        // Викторины
        List<Quiz> quizzes = quizRepository.findAll();
        assertFalse(quizzes.isEmpty(), "Quizzes should be preloaded");

        // Вопросы
        List<Question> questions = questionRepository.findAll();
        assertFalse(questions.isEmpty(), "Questions should be preloaded");

        // Варианты ответов
        List<AnswerOption> options = answerOptionRepository.findAll();
        assertFalse(options.isEmpty(), "Answer options should be preloaded");

        // Задания
        List<Assignment> assignments = assignmentRepository.findAll();
        assertFalse(assignments.isEmpty(), "Assignments should be preloaded");

        // Решения
        List<Submission> submissions = submissionRepository.findAll();
        assertFalse(submissions.isEmpty(), "Submissions should be preloaded");

        // Решения по викторинам
        List<QuizSubmission> quizSubmissions = quizSubmissionRepository.findAll();
        assertFalse(quizSubmissions.isEmpty(), "Quiz submissions should be preloaded");

        // Теги
        List<Tag> tags = tagRepository.findAll();
        assertFalse(tags.isEmpty(), "Tags should be preloaded");

        // Записи в таблице enrollments
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        assertFalse(enrollments.isEmpty(), "Enrollments should be preloaded");

        // Профили
        List<Profile> profiles = profileRepository.findAll();
        assertFalse(profiles.isEmpty(), "Profiles should be preloaded");
    }
}
