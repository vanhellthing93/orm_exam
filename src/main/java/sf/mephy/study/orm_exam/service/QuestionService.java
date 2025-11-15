package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.QuestionRequest;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.QuestionRepository;
import sf.mephy.study.orm_exam.repository.QuizRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question with id " + id + " not found"));
    }

    public Question createQuestion(Question question) {
        Long quizId = question.getQuiz().getId();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + quizId + " not found"));

        question.setQuiz(quiz);

        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, QuestionRequest questionRequest) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question with id " + id + " not found"));

        if (questionRequest.getText() != null) {
            existingQuestion.setText(questionRequest.getText());
        }

        if (questionRequest.getType() != null) {
            existingQuestion.setType(Question.QuestionType.valueOf(questionRequest.getType()));
        }

        if (questionRequest.getQuizId() != null) {
            Quiz quiz = quizRepository.findById(questionRequest.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz with id " + questionRequest.getQuizId() + " not found"));
            existingQuestion.setQuiz(quiz);
        }

        return questionRepository.save(existingQuestion);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question with id " + id + " not found"));
        questionRepository.delete(question);
    }
}
