package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.ModuleRequest;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.ModuleRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    public void testGetAllModules() {
        when(moduleRepository.findAll()).thenReturn(Arrays.asList(new Module(), new Module()));

        List<Module> modules = moduleService.getAllModules();

        assertEquals(2, modules.size());
        verify(moduleRepository).findAll();
    }

    @Test
    public void testGetModuleById_Success() {
        Module module = new Module();
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));

        Module found = moduleService.getModuleById(1L);

        assertNotNull(found);
        verify(moduleRepository).findById(1L);
    }

    @Test
    public void testGetModuleById_NotFound() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moduleService.getModuleById(1L));
    }

    @Test
    public void testCreateModule_Success() {
        Course course = new Course();
        course.setId(1L);

        Module module = new Module();
        module.setCourse(course);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArgument(0));

        Module created = moduleService.createModule(module);

        assertNotNull(created);
        assertEquals(course, created.getCourse());
        verify(courseRepository).findById(1L);
        verify(moduleRepository).save(module);
    }

    @Test
    public void testCreateModule_CourseNotFound() {
        Module module = new Module();
        Course course = new Course();
        course.setId(1L);
        module.setCourse(course);

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moduleService.createModule(module));
    }

    @Test
    public void testUpdateModule() {
        Module existing = new Module();
        existing.setTitle("Old title");
        existing.setOrderIndex(1);

        Course oldCourse = new Course();
        oldCourse.setId(1L);
        existing.setCourse(oldCourse);

        ModuleRequest request = new ModuleRequest();
        request.setTitle("New title");
        request.setOrderIndex(2);
        request.setCourseId(2L);

        Course newCourse = new Course();
        newCourse.setId(2L);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(newCourse));
        when(moduleRepository.save(existing)).thenReturn(existing);

        Module updated = moduleService.updateModule(1L, request);

        assertEquals("New title", updated.getTitle());
        assertEquals(2, updated.getOrderIndex());
        assertEquals(newCourse, updated.getCourse());
    }

    @Test
    public void testUpdateModule_NotFound() {
        ModuleRequest request = new ModuleRequest();
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moduleService.updateModule(1L, request));
    }

    @Test
    public void testUpdateModule_CourseNotFound() {
        Module existing = new Module();

        ModuleRequest request = new ModuleRequest();
        request.setCourseId(2L);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moduleService.updateModule(1L, request));
    }

    @Test
    public void testDeleteModule_Success() {
        Module module = new Module();
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        doNothing().when(moduleRepository).delete(module);

        moduleService.deleteModule(1L);

        verify(moduleRepository).delete(module);
    }

    @Test
    public void testDeleteModule_NotFound() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> moduleService.deleteModule(1L));
    }
}
