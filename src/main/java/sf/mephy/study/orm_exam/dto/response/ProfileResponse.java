package sf.mephy.study.orm_exam.dto.response;

import lombok.Data;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;

@Data
public class ProfileResponse {
    private Long id;
    private String bio;
    private String avatarUrl;
    private String contactInfo;
    private UserInfo user;
}
