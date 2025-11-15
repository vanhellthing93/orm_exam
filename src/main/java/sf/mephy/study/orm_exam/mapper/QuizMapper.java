package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.ModuleInfo;
import sf.mephy.study.orm_exam.dto.request.QuizRequest;
import sf.mephy.study.orm_exam.dto.response.QuizResponse;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.entity.Quiz;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ModuleMapper.class})
public interface QuizMapper {

    @Mapping(target = "module", source = "moduleId", qualifiedByName = "moduleIdToModule")
    Quiz toEntity(QuizRequest request);

    @Mapping(target = "module", source = "module", qualifiedByName = "moduleToModuleInfo")
    QuizResponse toResponse(Quiz quiz);

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
