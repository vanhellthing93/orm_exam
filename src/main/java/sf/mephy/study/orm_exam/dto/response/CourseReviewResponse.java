package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.CourseInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import java.time.LocalDateTime;

@Data
public class CourseReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private CourseInfo course;
    private UserInfo student;
}
