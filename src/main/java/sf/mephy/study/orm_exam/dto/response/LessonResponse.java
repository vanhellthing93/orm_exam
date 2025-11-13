package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.ModuleInfo;

@Data
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private ModuleInfo module;
}
