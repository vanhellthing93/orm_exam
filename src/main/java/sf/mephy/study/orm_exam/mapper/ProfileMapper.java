package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import sf.mephy.study.orm_exam.dto.request.ProfileRequest;
import sf.mephy.study.orm_exam.dto.response.ProfileResponse;
import sf.mephy.study.orm_exam.entity.Profile;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface ProfileMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
    Profile toEntity(ProfileRequest request);

    @Mapping(target = "user", source = "user", qualifiedByName = "profileUserToUserInfo")
    ProfileResponse toResponse(Profile profile);

    @Named("profileUserToUserInfo")
    default UserInfo profileUserToUserInfo(User user) {
        if (user == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        return userInfo;
    }

    @Named("userIdToUser")
    default User userIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
