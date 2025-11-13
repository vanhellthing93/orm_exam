package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.LessonInfo;
import java.time.LocalDate;

@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxScore;
    private LessonInfo lesson;
}
