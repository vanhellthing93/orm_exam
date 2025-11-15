package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.QuestionRequest;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.QuestionRepository;
import sf.mephy.study.orm_exam.repository.QuizRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    public void testGetAllQuestions() {
        when(questionRepository.findAll()).thenReturn(Arrays.asList(new Question(), new Question()));

        List<Question> questions = questionService.getAllQuestions();

        assertEquals(2, questions.size());
        verify(questionRepository).findAll();
    }

    @Test
    public void testGetQuestionById_Success() {
        Question question = new Question();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Question found = questionService.getQuestionById(1L);

        assertNotNull(found);
        verify(questionRepository).findById(1L);
    }

    @Test
    public void testGetQuestionById_NotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.getQuestionById(1L));
    }

    @Test
    public void testCreateQuestion_Success() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        Question question = new Question();
        question.setQuiz(quiz);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(any(Question.class))).thenAnswer(i -> i.getArgument(0));

        Question created = questionService.createQuestion(question);

        assertNotNull(created);
        assertEquals(quiz, created.getQuiz());
        verify(quizRepository).findById(1L);
        verify(questionRepository).save(question);
    }

    @Test
    public void testCreateQuestion_QuizNotFound() {
        Question question = new Question();
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        question.setQuiz(quiz);

        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.createQuestion(question));
    }

    @Test
    public void testUpdateQuestion() {
        Question existing = new Question();
        existing.setText("Old text");
        existing.setType(Question.QuestionType.MULTIPLE_CHOICE);

        Quiz oldQuiz = new Quiz();
        oldQuiz.setId(1L);
        existing.setQuiz(oldQuiz);

        QuestionRequest request = new QuestionRequest();
        request.setText("New text");
        request.setType("SINGLE_CHOICE");
        request.setQuizId(2L);

        Quiz newQuiz = new Quiz();
        newQuiz.setId(2L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(quizRepository.findById(2L)).thenReturn(Optional.of(newQuiz));
        when(questionRepository.save(existing)).thenReturn(existing);

        Question updated = questionService.updateQuestion(1L, request);

        assertEquals("New text", updated.getText());
        assertEquals(Question.QuestionType.SINGLE_CHOICE, updated.getType());
        assertEquals(newQuiz, updated.getQuiz());
    }

    @Test
    public void testUpdateQuestion_NotFound() {
        QuestionRequest request = new QuestionRequest();
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestion(1L, request));
    }

    @Test
    public void testUpdateQuestion_QuizNotFound() {
        Question existing = new Question();

        QuestionRequest request = new QuestionRequest();
        request.setQuizId(2L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(quizRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestion(1L, request));
    }

    @Test
    public void testDeleteQuestion_Success() {
        Question question = new Question();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).delete(question);

        questionService.deleteQuestion(1L);

        verify(questionRepository).delete(question);
    }

    @Test
    public void testDeleteQuestion_NotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> questionService.deleteQuestion(1L));
    }
}
