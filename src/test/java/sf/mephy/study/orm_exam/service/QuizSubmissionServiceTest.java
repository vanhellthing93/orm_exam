package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.QuizSubmissionRequest;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.entity.QuizSubmission;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.QuizRepository;
import sf.mephy.study.orm_exam.repository.QuizSubmissionRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizSubmissionServiceTest {

    @Mock
    private QuizSubmissionRepository quizSubmissionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizSubmissionService quizSubmissionService;

    @Test
    public void testGetAllQuizSubmissions() {
        when(quizSubmissionRepository.findAll()).thenReturn(Arrays.asList(new QuizSubmission(), new QuizSubmission()));

        List<QuizSubmission> submissions = quizSubmissionService.getAllQuizSubmissions();

        assertEquals(2, submissions.size());
        verify(quizSubmissionRepository).findAll();
    }

    @Test
    public void testGetQuizSubmissionById_Success() {
        QuizSubmission submission = new QuizSubmission();
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        QuizSubmission result = quizSubmissionService.getQuizSubmissionById(1L);

        assertNotNull(result);
        verify(quizSubmissionRepository).findById(1L);
    }

    @Test
    public void testGetQuizSubmissionById_NotFound() {
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.getQuizSubmissionById(1L));
    }

    @Test
    public void testGetQuizSubmissionsByStudentId() {
        List<QuizSubmission> list = Arrays.asList(new QuizSubmission(), new QuizSubmission());
        when(quizSubmissionRepository.findByStudentId(1L)).thenReturn(list);

        List<QuizSubmission> result = quizSubmissionService.getQuizSubmissionsByStudentId(1L);

        assertEquals(2, result.size());
        verify(quizSubmissionRepository).findByStudentId(1L);
    }

    @Test
    public void testGetQuizSubmissionsByCourseId() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        List<QuizSubmission> submissions = Arrays.asList(new QuizSubmission(), new QuizSubmission());

        when(quizRepository.findByModule_CourseId(1L)).thenReturn(quizzes);
        when(quizSubmissionRepository.findByQuizIdIn(quizzes.stream().map(Quiz::getId).collect(Collectors.toList())))
                .thenReturn(submissions);

        List<QuizSubmission> result = quizSubmissionService.getQuizSubmissionsByCourseId(1L);

        assertEquals(2, result.size());
        verify(quizRepository).findByModule_CourseId(1L);
        verify(quizSubmissionRepository).findByQuizIdIn(anyList());
    }

    @Test
    public void testGetQuizSubmissionsByModuleId() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        List<QuizSubmission> submissions = Arrays.asList(new QuizSubmission(), new QuizSubmission());

        when(quizRepository.findByModuleId(1L)).thenReturn(quizzes);
        when(quizSubmissionRepository.findByQuizIdIn(quizzes.stream().map(Quiz::getId).collect(Collectors.toList())))
                .thenReturn(submissions);

        List<QuizSubmission> result = quizSubmissionService.getQuizSubmissionsByModuleId(1L);

        assertEquals(2, result.size());
        verify(quizRepository).findByModuleId(1L);
        verify(quizSubmissionRepository).findByQuizIdIn(anyList());
    }

    @Test
    public void testCreateQuizSubmission() {
        QuizSubmission submission = new QuizSubmission();

        when(quizSubmissionRepository.save(submission)).thenReturn(submission);

        QuizSubmission created = quizSubmissionService.createQuizSubmission(submission);

        assertNotNull(created);
        verify(quizSubmissionRepository).save(submission);
    }

    @Test
    public void testSubmitQuiz_Success() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        User student = new User();
        student.setId(2L);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(i -> i.getArgument(0));

        QuizSubmission submission = quizSubmissionService.submitQuiz(1L, 2L, 95);

        assertNotNull(submission);
        assertEquals(quiz, submission.getQuiz());
        assertEquals(student, submission.getStudent());
        assertEquals(95, submission.getScore());
        assertNotNull(submission.getTakenAt());

        verify(quizRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(quizSubmissionRepository).save(any(QuizSubmission.class));
    }

    @Test
    public void testSubmitQuiz_QuizNotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.submitQuiz(1L, 2L, 95));
    }

    @Test
    public void testSubmitQuiz_StudentNotFound() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.submitQuiz(1L, 2L, 95));
    }

    @Test
    public void testUpdateQuizSubmission_Success() {
        QuizSubmission existing = new QuizSubmission();
        existing.setScore(80);

        Quiz oldQuiz = new Quiz();
        oldQuiz.setId(1L);
        existing.setQuiz(oldQuiz);

        User oldStudent = new User();
        oldStudent.setId(1L);
        existing.setStudent(oldStudent);

        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setScore(90);
        request.setQuizId(2L);
        request.setStudentId(3L);

        Quiz newQuiz = new Quiz();
        newQuiz.setId(2L);
        User newStudent = new User();
        newStudent.setId(3L);

        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(quizRepository.findById(2L)).thenReturn(Optional.of(newQuiz));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newStudent));
        when(quizSubmissionRepository.save(existing)).thenReturn(existing);

        QuizSubmission updated = quizSubmissionService.updateQuizSubmission(1L, request);

        assertEquals(90, updated.getScore());
        assertEquals(newQuiz, updated.getQuiz());
        assertEquals(newStudent, updated.getStudent());
    }

    @Test
    public void testUpdateQuizSubmission_NotFound() {
        QuizSubmissionRequest request = new QuizSubmissionRequest();
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.updateQuizSubmission(1L, request));
    }

    @Test
    public void testUpdateQuizSubmission_QuizNotFound() {
        QuizSubmission existing = new QuizSubmission();
        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setQuizId(2L);

        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(quizRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.updateQuizSubmission(1L, request));
    }

    @Test
    public void testUpdateQuizSubmission_StudentNotFound() {
        QuizSubmission existing = new QuizSubmission();
        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setStudentId(3L);

        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.updateQuizSubmission(1L, request));
    }

    @Test
    public void testDeleteQuizSubmission_Success() {
        QuizSubmission submission = new QuizSubmission();
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        doNothing().when(quizSubmissionRepository).delete(submission);

        quizSubmissionService.deleteQuizSubmission(1L);

        verify(quizSubmissionRepository).delete(submission);
    }

    @Test
    public void testDeleteQuizSubmission_NotFound() {
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> quizSubmissionService.deleteQuizSubmission(1L));
    }
}
