package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import sf.mephy.study.orm_exam.dto.request.UserRequest;
import sf.mephy.study.orm_exam.dto.response.UserResponse;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserRequest request);
    UserResponse toResponse(User user);

    @Named("userToUserInfo")
    default UserInfo userToUserInfo(User user) {
        if (user == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        return userInfo;
    }
}
