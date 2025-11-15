package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.QuizSubmissionRequest;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.entity.QuizSubmission;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.QuizRepository;
import sf.mephy.study.orm_exam.repository.QuizSubmissionRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public List<QuizSubmission> getAllQuizSubmissions() {
        return quizSubmissionRepository.findAll();
    }

    public QuizSubmission getQuizSubmissionById(Long id) {
        return quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizSubmission with id " + id + " not found"));
    }

    public List<QuizSubmission> getQuizSubmissionsByStudentId(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    public List<QuizSubmission> getQuizSubmissionsByCourseId(Long courseId) {
        List<Quiz> quizzes = quizRepository.findByModule_CourseId(courseId);
        List<Long> quizIds = quizzes.stream().map(Quiz::getId).collect(Collectors.toList());
        return quizSubmissionRepository.findByQuizIdIn(quizIds);
    }

    public List<QuizSubmission> getQuizSubmissionsByModuleId(Long moduleId) {
        List<Quiz> quizzes = quizRepository.findByModuleId(moduleId);
        List<Long> quizIds = quizzes.stream().map(Quiz::getId).collect(Collectors.toList());
        return quizSubmissionRepository.findByQuizIdIn(quizIds);
    }

    public QuizSubmission createQuizSubmission(QuizSubmission quizSubmission) {
        return quizSubmissionRepository.save(quizSubmission);
    }

    public QuizSubmission submitQuiz(Long quizId, Long studentId, Integer score) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + quizId + " not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + studentId + " not found"));

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);
        quizSubmission.setScore(score);
        quizSubmission.setTakenAt(LocalDateTime.now());

        return quizSubmissionRepository.save(quizSubmission);
    }

    public QuizSubmission updateQuizSubmission(Long id, QuizSubmissionRequest quizSubmissionRequest) {
        QuizSubmission existingSubmission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizSubmission with id " + id + " not found"));

        if (quizSubmissionRequest.getScore() != null) {
            existingSubmission.setScore(quizSubmissionRequest.getScore());
        }

        if (quizSubmissionRequest.getQuizId() != null) {
            Quiz quiz = quizRepository.findById(quizSubmissionRequest.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + quizSubmissionRequest.getQuizId() + " not found"));
            existingSubmission.setQuiz(quiz);
        }

        if (quizSubmissionRequest.getStudentId() != null) {
            User student = userRepository.findById(quizSubmissionRequest.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + quizSubmissionRequest.getStudentId() + " not found"));
            existingSubmission.setStudent(student);
        }

        return quizSubmissionRepository.save(existingSubmission);
    }

    public void deleteQuizSubmission(Long id) {
        QuizSubmission quizSubmission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizSubmission with id " + id + " not found"));
        quizSubmissionRepository.delete(quizSubmission);
    }
}
