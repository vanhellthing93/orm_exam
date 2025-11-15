package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.QuizInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import sf.mephy.study.orm_exam.dto.request.QuizSubmissionRequest;
import sf.mephy.study.orm_exam.dto.response.QuizSubmissionResponse;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.entity.QuizSubmission;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {QuizMapper.class, UserMapper.class})
public interface QuizSubmissionMapper {

    @Mapping(target = "quiz", source = "quizId", qualifiedByName = "quizIdToQuiz")
    @Mapping(target = "student", source = "studentId", qualifiedByName = "studentIdToUser")
    QuizSubmission toEntity(QuizSubmissionRequest request);

    @Mapping(target = "quiz", source = "quiz", qualifiedByName = "quizToQuizInfo")
    @Mapping(target = "student", source = "student", qualifiedByName = "userToUserInfo")
    QuizSubmissionResponse toResponse(QuizSubmission quizSubmission);

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
