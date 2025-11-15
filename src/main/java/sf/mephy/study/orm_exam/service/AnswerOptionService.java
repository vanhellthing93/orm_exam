package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.AnswerOption;
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
}
