package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.QuizRequest;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.ModuleRepository;
import sf.mephy.study.orm_exam.repository.QuizRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;

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
}
