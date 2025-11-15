package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class QuizRequest {
    private String title;
    private Integer timeLimit;
    private Long moduleId;
}
