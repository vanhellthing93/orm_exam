package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AnswerOptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerOptionService {
    private final AnswerOptionRepository answerOptionRepository;

    public List<AnswerOption> getAllAnswerOptions() {
        return answerOptionRepository.findAll();
    }

    public AnswerOption getAnswerOptionById(Long id) {
        return answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AnswerOption with id " + id + " not found"));
    }

    public AnswerOption createAnswerOption(AnswerOption answerOption) {
        return answerOptionRepository.save(answerOption);
    }
}
