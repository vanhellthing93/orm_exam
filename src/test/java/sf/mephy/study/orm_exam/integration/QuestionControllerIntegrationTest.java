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
import sf.mephy.study.orm_exam.dto.request.AnswerOptionRequest;
import sf.mephy.study.orm_exam.dto.request.QuestionRequest;
import sf.mephy.study.orm_exam.dto.response.QuestionResponse;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.repository.QuestionRepository;
import sf.mephy.study.orm_exam.repository.QuizRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuestionControllerIntegrationTest {

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
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Quiz quiz;

    @BeforeEach
    public void setup() {
        questionRepository.deleteAll();
        quizRepository.deleteAll();

        quiz = new Quiz();
        quiz.setTitle("Sample Quiz");
        quiz = quizRepository.save(quiz);
    }

    @Test
    public void testCreateQuestionWithOptions() throws Exception {
        QuestionRequest request = new QuestionRequest();
        request.setText("What is 2+2?");
        request.setType("SINGLE_CHOICE");
        request.setQuizId(quiz.getId());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("What is 2+2?"));
    }

    @Test
    public void testGetQuestionById() throws Exception {
        Question question = new Question();
        question.setText("Sample question");
        question.setType(Question.QuestionType.valueOf("SINGLE_CHOICE"));
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        mockMvc.perform(get("/api/v1/questions/{id}", question.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Sample question"));
    }

    @Test
    public void testGetAllQuestions() throws Exception {
        Question q1 = new Question();
        q1.setText("Q1");
        q1.setType(Question.QuestionType.valueOf("SINGLE_CHOICE"));
        q1.setQuiz(quiz);

        Question q2 = new Question();
        q2.setText("Q2");
        q2.setType(Question.QuestionType.valueOf("MULTIPLE_CHOICE"));
        q2.setQuiz(quiz);

        questionRepository.saveAll(List.of(q1, q2));

        mockMvc.perform(get("/api/v1/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void testUpdateQuestion() throws Exception {
        Question question = new Question();
        question.setText("Old Text");
        question.setType(Question.QuestionType.valueOf("SINGLE_CHOICE"));
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        QuestionRequest updateRequest = new QuestionRequest();
        updateRequest.setText("New Text");
        updateRequest.setType("MULTIPLE_CHOICE");
        updateRequest.setQuizId(quiz.getId());

        String json = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/v1/questions/{id}", question.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New Text"))
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"));
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        Question question = new Question();
        question.setText("To delete");
        question.setType(Question.QuestionType.valueOf("SINGLE_CHOICE"));
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        mockMvc.perform(delete("/api/v1/questions/{id}", question.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/questions/{id}", question.getId()))
                .andExpect(status().isNotFound());
    }
}
