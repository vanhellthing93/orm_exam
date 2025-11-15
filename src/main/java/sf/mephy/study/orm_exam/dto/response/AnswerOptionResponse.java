package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;

@Data
public class AnswerOptionResponse {
    private Long id;
    private String text;
    private Boolean isCorrect;
}
