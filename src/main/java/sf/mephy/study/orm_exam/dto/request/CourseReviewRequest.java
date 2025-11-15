package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class CourseReviewRequest {
    private Integer rating;
    private String comment;
    private Long courseId;
    private Long studentId;
}
