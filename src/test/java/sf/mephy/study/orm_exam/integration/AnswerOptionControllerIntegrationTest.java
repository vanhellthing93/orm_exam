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
import sf.mephy.study.orm_exam.dto.response.AnswerOptionResponse;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.repository.AnswerOptionRepository;
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
public class AnswerOptionControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName(System.getenv().getOrDefault("DB_NAME", "testdb"))
            .withUsername(System.getenv().getOrDefault("DB_USERNAME", "testuser"))
            .withPassword(System.getenv().getOrDefault("DB_PASSWORD", "testpass"));
    @Autowired
    private QuizRepository quizRepository;

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
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Question question;

    @BeforeEach
    public void setup() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Samle quiz");
        quiz = quizRepository.save(quiz);

        question = new Question();
        question.setText("Sample Question");
        question.setType(Question.QuestionType.SINGLE_CHOICE);
        question.setQuiz(quiz);
        question = questionRepository.save(question);
    }

    @Test
    public void testCreateAnswerOption() throws Exception {
        AnswerOptionRequest request = new AnswerOptionRequest();
        request.setText("Option A");
        request.setIsCorrect(true);
        request.setQuestionId(question.getId());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/answer-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Option A"))
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    public void testGetAnswerOptionById() throws Exception {
        AnswerOption option = new AnswerOption();
        option.setText("Option X");
        option.setIsCorrect(false);
        option.setQuestion(question);

        option = answerOptionRepository.save(option);

        mockMvc.perform(get("/api/v1/answer-options/{id}", option.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Option X"))
                .andExpect(jsonPath("$.isCorrect").value(false));
    }

    @Test
    public void testGetAllAnswerOptions() throws Exception {
        AnswerOption option1 = new AnswerOption();
        option1.setText("Option 1");
        option1.setIsCorrect(true);
        option1.setQuestion(question);

        AnswerOption option2 = new AnswerOption();
        option2.setText("Option 2");
        option2.setIsCorrect(false);
        option2.setQuestion(question);

        answerOptionRepository.saveAll(List.of(option1, option2));

        mockMvc.perform(get("/api/v1/answer-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].text", anyOf(is("Option 1"), is("Option 2"))));
    }

    @Test
    public void testUpdateAnswerOption() throws Exception {
        AnswerOption option = new AnswerOption();
        option.setText("Old Option");
        option.setIsCorrect(false);
        option.setQuestion(question);
        option = answerOptionRepository.save(option);

        AnswerOptionRequest request = new AnswerOptionRequest();
        request.setText("Updated Option");
        request.setIsCorrect(true);
        request.setQuestionId(question.getId());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/answer-options/{id}", option.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated Option"))
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    public void testDeleteAnswerOption() throws Exception {
        AnswerOption option = new AnswerOption();
        option.setText("Delete Me");
        option.setIsCorrect(true);
        option.setQuestion(question);
        option = answerOptionRepository.save(option);

        mockMvc.perform(delete("/api/v1/answer-options/{id}", option.getId()))
                .andExpect(status().isNoContent());

        // Проверяем, что опция удалена
        mockMvc.perform(get("/api/v1/answer-options/{id}", option.getId()))
                .andExpect(status().isNotFound());
    }
}
