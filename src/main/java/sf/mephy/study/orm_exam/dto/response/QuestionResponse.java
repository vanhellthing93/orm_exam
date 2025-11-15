package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.QuizInfo;
import java.util.List;

@Data
public class QuestionResponse {
    private Long id;
    private String text;
    private String type;
    private QuizInfo quiz;
    private List<AnswerOptionResponse> options;
}
