package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.LessonInfo;
import sf.mephy.study.orm_exam.dto.request.AssignmentRequest;
import sf.mephy.study.orm_exam.dto.response.AssignmentResponse;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Lesson;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {LessonMapper.class})
public interface AssignmentMapper {

    @Mapping(target = "lesson", source = "lessonId", qualifiedByName = "lessonIdToLesson")
    Assignment toEntity(AssignmentRequest request);

    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonToLessonInfo")
    AssignmentResponse toResponse(Assignment assignment);

    @Named("lessonToLessonInfo")
    default LessonInfo lessonToLessonInfo(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        LessonInfo lessonInfo = new LessonInfo();
        lessonInfo.setId(lesson.getId());
        lessonInfo.setTitle(lesson.getTitle());
        return lessonInfo;
    }

    @Named("lessonIdToLesson")
    default Lesson lessonIdToLesson(Long lessonId) {
        if (lessonId == null) {
            return null;
        }
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        return lesson;
    }
}
