package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.ModuleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Module with id " + id + " not found"));
    }

    public Module createModule(Module module) {
        return moduleRepository.save(module);
    }
}
