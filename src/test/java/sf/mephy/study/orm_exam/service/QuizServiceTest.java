package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.QuizRequest;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.ModuleRepository;
import sf.mephy.study.orm_exam.repository.QuizRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    public void testGetAllQuizzes() {
        when(quizRepository.findAll()).thenReturn(Arrays.asList(new Quiz(), new Quiz()));

        List<Quiz> quizzes = quizService.getAllQuizzes();

        assertEquals(2, quizzes.size());
        verify(quizRepository).findAll();
    }

    @Test
    public void testGetQuizById_Success() {
        Quiz quiz = new Quiz();
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        Quiz found = quizService.getQuizById(1L);

        assertNotNull(found);
        verify(quizRepository).findById(1L);
    }

    @Test
    public void testGetQuizById_NotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.getQuizById(1L));
    }

    @Test
    public void testCreateQuiz_Success() {
        Module module = new Module();
        module.setId(1L);

        Quiz quiz = new Quiz();
        quiz.setModule(module);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Quiz created = quizService.createQuiz(quiz);

        assertNotNull(created);
        assertEquals(module, created.getModule());
        verify(moduleRepository).findById(1L);
        verify(quizRepository).save(quiz);
    }

    @Test
    public void testCreateQuiz_ModuleNotFound() {
        Quiz quiz = new Quiz();
        Module module = new Module();
        module.setId(1L);
        quiz.setModule(module);

        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.createQuiz(quiz));
    }

    @Test
    public void testUpdateQuiz() {
        Quiz existing = new Quiz();
        existing.setTitle("Old title");
        existing.setTimeLimit(30);

        Module oldModule = new Module();
        oldModule.setId(1L);
        existing.setModule(oldModule);

        QuizRequest request = new QuizRequest();
        request.setTitle("New title");
        request.setTimeLimit(60);
        request.setModuleId(2L);

        Module newModule = new Module();
        newModule.setId(2L);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(moduleRepository.findById(2L)).thenReturn(Optional.of(newModule));
        when(quizRepository.save(existing)).thenReturn(existing);

        Quiz updated = quizService.updateQuiz(1L, request);

        assertEquals("New title", updated.getTitle());
        assertEquals(60, updated.getTimeLimit());
        assertEquals(newModule, updated.getModule());
    }

    @Test
    public void testUpdateQuiz_NotFound() {
        QuizRequest request = new QuizRequest();
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.updateQuiz(1L, request));
    }

    @Test
    public void testUpdateQuiz_ModuleNotFound() {
        Quiz existing = new Quiz();

        QuizRequest request = new QuizRequest();
        request.setModuleId(2L);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(moduleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.updateQuiz(1L, request));
    }

    @Test
    public void testDeleteQuiz_Success() {
        Quiz quiz = new Quiz();
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        doNothing().when(quizRepository).delete(quiz);

        quizService.deleteQuiz(1L);

        verify(quizRepository).delete(quiz);
    }

    @Test
    public void testDeleteQuiz_NotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizService.deleteQuiz(1L));
    }
}
