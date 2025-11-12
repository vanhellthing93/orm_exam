package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.CategoryInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import java.time.LocalDate;

@Data
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private UserInfo teacher;
    private CategoryInfo category;
    private LocalDate startDate;
    private Integer duration;
}
