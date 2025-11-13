package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String content;
    private Long assignmentId;
    private Long studentId;
}
