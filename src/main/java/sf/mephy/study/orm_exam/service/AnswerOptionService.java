package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.AnswerOptionRequest;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AnswerOptionRepository;
import sf.mephy.study.orm_exam.repository.QuestionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerOptionService {
    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;

    public List<AnswerOption> getAllAnswerOptions() {
        return answerOptionRepository.findAll();
    }

    public AnswerOption getAnswerOptionById(Long id) {
        return answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption with id " + id + " not found"));
    }

    public AnswerOption createAnswerOption(AnswerOption answerOption) {
        Long questionId = answerOption.getQuestion().getId();

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with id " + questionId + " not found"));

        answerOption.setQuestion(question);

        return answerOptionRepository.save(answerOption);
    }

    public AnswerOption updateAnswerOption(Long id, AnswerOptionRequest answerOptionRequest) {
        AnswerOption existingAnswerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption with id " + id + " not found"));

        if (answerOptionRequest.getText() != null) {
            existingAnswerOption.setText(answerOptionRequest.getText());
        }

        if (answerOptionRequest.getIsCorrect() != null) {
            existingAnswerOption.setIsCorrect(answerOptionRequest.getIsCorrect());
        }

        if (answerOptionRequest.getQuestionId() != null) {
            Question question = questionRepository.findById(answerOptionRequest.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question with id " + answerOptionRequest.getQuestionId() + " not found"));
            existingAnswerOption.setQuestion(question);
        }

        return answerOptionRepository.save(existingAnswerOption);
    }

    public void deleteAnswerOption(Long id) {
        AnswerOption answerOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption with id " + id + " not found"));
        answerOptionRepository.delete(answerOption);
    }
}
