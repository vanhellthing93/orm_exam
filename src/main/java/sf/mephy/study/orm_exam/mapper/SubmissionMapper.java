package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.AssignmentInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import sf.mephy.study.orm_exam.dto.request.SubmissionRequest;
import sf.mephy.study.orm_exam.dto.response.SubmissionResponse;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Submission;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AssignmentMapper.class, UserMapper.class})
public interface SubmissionMapper {

    @Mapping(target = "assignment", source = "assignmentId", qualifiedByName = "assignmentIdToAssignment")
    @Mapping(target = "student", source = "studentId", qualifiedByName = "studentIdToUser")
    Submission toEntity(SubmissionRequest request);

    @Mapping(target = "assignment", source = "assignment", qualifiedByName = "assignmentToAssignmentInfo")
    @Mapping(target = "student", source = "student", qualifiedByName = "userToUserInfo")
    SubmissionResponse toResponse(Submission submission);

    @Named("assignmentToAssignmentInfo")
    default AssignmentInfo assignmentToAssignmentInfo(Assignment assignment) {
        if (assignment == null) {
            return null;
        }
        AssignmentInfo assignmentInfo = new AssignmentInfo();
        assignmentInfo.setId(assignment.getId());
        assignmentInfo.setTitle(assignment.getTitle());
        return assignmentInfo;
    }

    @Named("assignmentIdToAssignment")
    default Assignment assignmentIdToAssignment(Long assignmentId) {
        if (assignmentId == null) {
            return null;
        }
        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        return assignment;
    }

    @Named("studentIdToUser")
    default User studentIdToUser(Long studentId) {
        if (studentId == null) {
            return null;
        }
        User user = new User();
        user.setId(studentId);
        return user;
    }
}
