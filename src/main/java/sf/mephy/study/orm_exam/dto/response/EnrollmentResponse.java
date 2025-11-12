package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.CourseInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import java.time.LocalDateTime;

@Data
public class EnrollmentResponse {
    private Long id;
    private UserInfo user;
    private CourseInfo course;
    private LocalDateTime enrollDate;
    private String status;
}
