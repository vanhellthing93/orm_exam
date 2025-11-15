package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.QuizInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import java.time.LocalDateTime;

@Data
public class QuizSubmissionResponse {
    private Long id;
    private Integer score;
    private LocalDateTime takenAt;
    private QuizInfo quiz;
    private UserInfo student;
}
