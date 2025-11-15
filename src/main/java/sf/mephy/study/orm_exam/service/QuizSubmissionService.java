package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.entity.QuizSubmission;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.QuizRepository;
import sf.mephy.study.orm_exam.repository.QuizSubmissionRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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
}
