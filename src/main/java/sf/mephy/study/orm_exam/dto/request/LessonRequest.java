package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class LessonRequest {
    private String title;
    private String content;
    private Long moduleId;
}
