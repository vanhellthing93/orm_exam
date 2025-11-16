package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sf.mephy.study.orm_exam.dto.request.QuizRequest;
import sf.mephy.study.orm_exam.entity.*;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.*;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;


    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + id + " not found"));
    }

    public Quiz createQuiz(Quiz quiz) {
        Long moduleId = quiz.getModule().getId();

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module with id " + moduleId + " not found"));

        quiz.setModule(module);


        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Long id, QuizRequest quizRequest) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + id + " not found"));

        if (quizRequest.getTitle() != null) {
            existingQuiz.setTitle(quizRequest.getTitle());
        }

        if (quizRequest.getTimeLimit() != null) {
            existingQuiz.setTimeLimit(quizRequest.getTimeLimit());
        }

        if (quizRequest.getModuleId() != null) {
            Module module = moduleRepository.findById(quizRequest.getModuleId())
                    .orElseThrow(() -> new EntityNotFoundException("Module with id " + quizRequest.getModuleId() + " not found"));
            existingQuiz.setModule(module);
        }

        return quizRepository.save(existingQuiz);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + id + " not found"));
        quizRepository.delete(quiz);
    }

    @Transactional
    public QuizSubmission takeQuiz(Long studentId, Long quizId, Map<Long, Long> answers) {
        // Получаем студента и тест
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with id " + studentId + " not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + quizId + " not found"));

        // Получаем вопросы теста
        List<Question> questions = questionRepository.findAllByQuiz_Id(quizId);

        // Проверяем ответы и подсчитываем баллы
        int totalScore = 0;
        for (Question question : questions) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId != null) {
                AnswerOption selectedOption = answerOptionRepository.findById(selectedOptionId)
                        .orElseThrow(() -> new EntityNotFoundException("AnswerOption with id " + selectedOptionId + " not found"));

                if (selectedOption.getIsCorrect()) {
                    totalScore += 1;
                }
            }
        }

        // Сохраняем результат теста
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setScore(totalScore);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(student);

        return quizSubmissionRepository.save(quizSubmission);
    }
}
