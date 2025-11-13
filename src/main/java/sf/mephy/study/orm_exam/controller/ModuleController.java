package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.ModuleRequest;
import sf.mephy.study.orm_exam.dto.response.ModuleResponse;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.mapper.ModuleMapper;
import sf.mephy.study.orm_exam.service.ModuleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;
    private final ModuleMapper moduleMapper;

    @GetMapping
    public List<ModuleResponse> getAllModules() {
        return moduleService.getAllModules().stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ModuleResponse getModuleById(@PathVariable Long id) {
        Module module = moduleService.getModuleById(id);
        return moduleMapper.toResponse(module);
    }

    @PostMapping
    public ModuleResponse createModule(@RequestBody ModuleRequest moduleRequest) {
        Module module = moduleMapper.toEntity(moduleRequest);
        Module createdModule = moduleService.createModule(module);
        return moduleMapper.toResponse(createdModule);
    }
}
