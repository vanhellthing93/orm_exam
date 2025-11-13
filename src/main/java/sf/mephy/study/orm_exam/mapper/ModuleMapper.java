package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.CourseInfo;
import sf.mephy.study.orm_exam.dto.request.ModuleRequest;
import sf.mephy.study.orm_exam.dto.response.ModuleResponse;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.Module;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {CourseMapper.class})
public interface ModuleMapper {

    @Mapping(target = "course", source = "courseId", qualifiedByName = "courseIdToCourse")
    Module toEntity(ModuleRequest request);

    @Mapping(target = "course", source = "course", qualifiedByName = "courseToCourseInfo")
    ModuleResponse toResponse(Module module);

    @Named("courseToCourseInfo")
    default CourseInfo courseToCourseInfo(Course course) {
        if (course == null) {
            return null;
        }
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setId(course.getId());
        courseInfo.setTitle(course.getTitle());
        return courseInfo;
    }

    @Named("courseIdToCourse")
    default Course courseIdToCourse(Long courseId) {
        if (courseId == null) {
            return null;
        }
        Course course = new Course();
        course.setId(courseId);
        return course;
    }
}
