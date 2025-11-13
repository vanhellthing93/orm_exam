package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class ModuleRequest {
    private String title;
    private Integer orderIndex;
    private Long courseId;
}
