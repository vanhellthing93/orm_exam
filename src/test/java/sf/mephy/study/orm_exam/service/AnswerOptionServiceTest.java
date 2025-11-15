package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.AnswerOptionRequest;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AnswerOptionRepository;
import sf.mephy.study.orm_exam.repository.QuestionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnswerOptionServiceTest {

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AnswerOptionService answerOptionService;

    @Test
    public void testGetAllAnswerOptions() {
        when(answerOptionRepository.findAll()).thenReturn(Arrays.asList(new AnswerOption(), new AnswerOption()));

        List<AnswerOption> result = answerOptionService.getAllAnswerOptions();

        assertEquals(2, result.size());
        verify(answerOptionRepository, times(1)).findAll();
    }

    @Test
    public void testGetAnswerOptionById_Success() {
        AnswerOption answerOption = new AnswerOption();
        when(answerOptionRepository.findById(1L)).thenReturn(Optional.of(answerOption));

        AnswerOption result = answerOptionService.getAnswerOptionById(1L);

        assertNotNull(result);
        verify(answerOptionRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAnswerOptionById_NotFound() {
        when(answerOptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerOptionService.getAnswerOptionById(1L));
    }

    @Test
    public void testCreateAnswerOption_Success() {
        Question question = new Question();
        question.setId(1L);

        AnswerOption answerOption = new AnswerOption();
        answerOption.setQuestion(question);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.save(any(AnswerOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnswerOption created = answerOptionService.createAnswerOption(answerOption);

        assertNotNull(created);
        assertEquals(question, created.getQuestion());
        verify(questionRepository).findById(1L);
        verify(answerOptionRepository).save(answerOption);
    }

    @Test
    public void testCreateAnswerOption_QuestionNotFound() {
        AnswerOption answerOption = new AnswerOption();
        Question question = new Question();
        question.setId(1L);
        answerOption.setQuestion(question);

        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerOptionService.createAnswerOption(answerOption));
    }

    @Test
    public void testUpdateAnswerOption_Success() {
        AnswerOption existing = new AnswerOption();
        existing.setText("Old text");
        existing.setIsCorrect(false);

        Question oldQuestion = new Question();
        oldQuestion.setId(1L);
        existing.setQuestion(oldQuestion);

        AnswerOptionRequest request = new AnswerOptionRequest();
        request.setText("New text");
        request.setIsCorrect(true);
        request.setQuestionId(2L);

        Question newQuestion = new Question();
        newQuestion.setId(2L);

        when(answerOptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(questionRepository.findById(2L)).thenReturn(Optional.of(newQuestion));
        when(answerOptionRepository.save(existing)).thenReturn(existing);

        AnswerOption updated = answerOptionService.updateAnswerOption(1L, request);

        assertEquals("New text", updated.getText());
        assertTrue(updated.getIsCorrect());
        assertEquals(newQuestion, updated.getQuestion());
    }

    @Test
    public void testUpdateAnswerOption_NotFound() {
        AnswerOptionRequest request = new AnswerOptionRequest();
        when(answerOptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerOptionService.updateAnswerOption(1L, request));
    }

    @Test
    public void testUpdateAnswerOption_QuestionNotFound() {
        AnswerOption existing = new AnswerOption();
        existing.setText("Old text");
        existing.setIsCorrect(false);

        AnswerOptionRequest request = new AnswerOptionRequest();
        request.setQuestionId(2L);

        when(answerOptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(questionRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerOptionService.updateAnswerOption(1L, request));
    }

    @Test
    public void testDeleteAnswerOption_Success() {
        AnswerOption answerOption = new AnswerOption();
        when(answerOptionRepository.findById(1L)).thenReturn(Optional.of(answerOption));
        doNothing().when(answerOptionRepository).delete(answerOption);

        answerOptionService.deleteAnswerOption(1L);

        verify(answerOptionRepository).delete(answerOption);
    }

    @Test
    public void testDeleteAnswerOption_NotFound() {
        when(answerOptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerOptionService.deleteAnswerOption(1L));
    }
}
