package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.request.AnswerOptionRequest;
import sf.mephy.study.orm_exam.dto.response.AnswerOptionResponse;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.entity.Question;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerOptionMapper {

    @Mapping(target = "question", source = "questionId", qualifiedByName = "questionIdToQuestion")
    AnswerOption toEntity(AnswerOptionRequest request);

    AnswerOptionResponse toResponse(AnswerOption answerOption);

    @Named("questionIdToQuestion")
    default Question questionIdToQuestion(Long questionId) {
        if (questionId == null) {
            return null;
        }
        Question question = new Question();
        question.setId(questionId);
        return question;
    }
}
