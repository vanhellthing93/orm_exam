package sf.mephy.study.orm_exam.dto.request;

import lombok.Data;

@Data
public class ProfileRequest {
    private String bio;
    private String avatarUrl;
    private String contactInfo;
    private Long userId;
}
