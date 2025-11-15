package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.QuizInfo;
import sf.mephy.study.orm_exam.dto.request.QuestionRequest;
import sf.mephy.study.orm_exam.dto.response.QuestionResponse;
import sf.mephy.study.orm_exam.entity.Question;
import sf.mephy.study.orm_exam.entity.Quiz;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuizMapper.class})
public interface QuestionMapper {

    @Mapping(target = "quiz", source = "quizId", qualifiedByName = "quizIdToQuiz")
    @Mapping(target = "type", expression = "java(sf.mephy.study.orm_exam.entity.Question.QuestionType.valueOf(request.getType()))")
    Question toEntity(QuestionRequest request);

    @Mapping(target = "quiz", source = "quiz", qualifiedByName = "quizToQuizInfo")
    @Mapping(target = "type", expression = "java(question.getType().name())")
    QuestionResponse toResponse(Question question);

    @Named("quizToQuizInfo")
    default QuizInfo quizToQuizInfo(Quiz quiz) {
        if (quiz == null) {
            return null;
        }
        QuizInfo quizInfo = new QuizInfo();
        quizInfo.setId(quiz.getId());
        quizInfo.setTitle(quiz.getTitle());
        return quizInfo;
    }

    @Named("quizIdToQuiz")
    default Quiz quizIdToQuiz(Long quizId) {
        if (quizId == null) {
            return null;
        }
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        return quiz;
    }
}
