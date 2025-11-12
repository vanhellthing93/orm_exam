package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String role;
}
