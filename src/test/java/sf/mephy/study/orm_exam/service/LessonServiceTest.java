package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.LessonRequest;
import sf.mephy.study.orm_exam.entity.Lesson;
import sf.mephy.study.orm_exam.entity.Module;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.LessonRepository;
import sf.mephy.study.orm_exam.repository.ModuleRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private LessonService lessonService;

    @Test
    public void testGetAllLessons() {
        when(lessonRepository.findAll()).thenReturn(Arrays.asList(new Lesson(), new Lesson()));

        List<Lesson> lessons = lessonService.getAllLessons();

        assertEquals(2, lessons.size());
        verify(lessonRepository).findAll();
    }

    @Test
    public void testGetLessonById_Success() {
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson found = lessonService.getLessonById(1L);

        assertNotNull(found);
        verify(lessonRepository).findById(1L);
    }

    @Test
    public void testGetLessonById_NotFound() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.getLessonById(1L));
    }

    @Test
    public void testCreateLesson_Success() {
        Module module = new Module();
        module.setId(1L);

        Lesson lesson = new Lesson();
        lesson.setModule(module);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        Lesson created = lessonService.createLesson(lesson);

        assertNotNull(created);
        assertEquals(module, created.getModule());
        verify(moduleRepository).findById(1L);
        verify(lessonRepository).save(lesson);
    }

    @Test
    public void testCreateLesson_ModuleNotFound() {
        Lesson lesson = new Lesson();
        Module module = new Module();
        module.setId(1L);
        lesson.setModule(module);

        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.createLesson(lesson));
    }

    @Test
    public void testUpdateLesson() {
        Lesson existing = new Lesson();
        existing.setTitle("Old title");
        existing.setContent("Old content");

        Module oldModule = new Module();
        oldModule.setId(1L);
        existing.setModule(oldModule);

        LessonRequest request = new LessonRequest();
        request.setTitle("New title");
        request.setContent("New content");
        request.setModuleId(2L);

        Module newModule = new Module();
        newModule.setId(2L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(moduleRepository.findById(2L)).thenReturn(Optional.of(newModule));
        when(lessonRepository.save(existing)).thenReturn(existing);

        Lesson updated = lessonService.updateLesson(1L, request);

        assertEquals("New title", updated.getTitle());
        assertEquals("New content", updated.getContent());
        assertEquals(newModule, updated.getModule());
    }

    @Test
    public void testUpdateLesson_NotFound() {
        LessonRequest request = new LessonRequest();
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.updateLesson(1L, request));
    }

    @Test
    public void testUpdateLesson_ModuleNotFound() {
        Lesson existing = new Lesson();

        LessonRequest request = new LessonRequest();
        request.setModuleId(2L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(moduleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.updateLesson(1L, request));
    }

    @Test
    public void testDeleteLesson_Success() {
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        doNothing().when(lessonRepository).delete(lesson);

        lessonService.deleteLesson(1L);

        verify(lessonRepository).delete(lesson);
    }

    @Test
    public void testDeleteLesson_NotFound() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.deleteLesson(1L));
    }
}
