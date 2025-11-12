package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import sf.mephy.study.orm_exam.dto.request.UserRequest;
import sf.mephy.study.orm_exam.dto.response.UserResponse;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserRequest request);
    UserResponse toResponse(User user);
}
