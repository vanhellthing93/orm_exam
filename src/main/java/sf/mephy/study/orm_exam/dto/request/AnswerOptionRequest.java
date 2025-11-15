package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class AnswerOptionRequest {
    private String text;
    private Boolean isCorrect;
    private Long questionId;
}
