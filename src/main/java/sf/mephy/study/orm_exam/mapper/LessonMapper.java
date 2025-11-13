package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.ModuleInfo;
import sf.mephy.study.orm_exam.dto.request.LessonRequest;
import sf.mephy.study.orm_exam.dto.response.LessonResponse;
import sf.mephy.study.orm_exam.entity.Lesson;
import sf.mephy.study.orm_exam.entity.Module;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ModuleMapper.class})
public interface LessonMapper {

    @Mapping(target = "module", source = "moduleId", qualifiedByName = "moduleIdToModule")
    Lesson toEntity(LessonRequest request);

    @Mapping(target = "module", source = "module", qualifiedByName = "moduleToModuleInfo")
    LessonResponse toResponse(Lesson lesson);

    @Named("moduleToModuleInfo")
    default ModuleInfo moduleToModuleInfo(Module module) {
        if (module == null) {
            return null;
        }
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.setId(module.getId());
        moduleInfo.setTitle(module.getTitle());
        return moduleInfo;
    }

    @Named("moduleIdToModule")
    default Module moduleIdToModule(Long moduleId) {
        if (moduleId == null) {
            return null;
        }
        Module module = new Module();
        module.setId(moduleId);
        return module;
    }
}
