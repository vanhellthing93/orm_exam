package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.AssignmentRequest;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Lesson;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AssignmentRepository;
import sf.mephy.study.orm_exam.repository.LessonRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    @Test
    public void testGetAllAssignments() {
        when(assignmentRepository.findAll()).thenReturn(Arrays.asList(new Assignment(), new Assignment()));

        List<Assignment> assignments = assignmentService.getAllAssignments();

        assertEquals(2, assignments.size());
        verify(assignmentRepository, times(1)).findAll();
    }

    @Test
    public void testGetAssignmentById_Success() {
        Assignment assignment = new Assignment();
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        Assignment found = assignmentService.getAssignmentById(1L);

        assertNotNull(found);
        verify(assignmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAssignmentById_NotFound() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> assignmentService.getAssignmentById(1L));
    }

    @Test
    public void testCreateAssignment_Success() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);

        Assignment assignment = new Assignment();
        assignment.setLesson(lesson);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Assignment created = assignmentService.createAssignment(assignment);

        assertNotNull(created);
        assertEquals(lesson, created.getLesson());
        verify(lessonRepository).findById(1L);
        verify(assignmentRepository).save(assignment);
    }

    @Test
    public void testCreateAssignment_LessonNotFound() {
        Assignment assignment = new Assignment();
        Lesson lesson = new Lesson();
        lesson.setId(1L);
        assignment.setLesson(lesson);

        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> assignmentService.createAssignment(assignment));
    }

    @Test
    public void testUpdateAssignment() {
        Assignment existing = new Assignment();
        existing.setTitle("Old title");
        existing.setDescription("Old description");
        existing.setDueDate(LocalDate.of(2025, 1, 1));
        existing.setMaxScore(10);

        Lesson oldLesson = new Lesson();
        oldLesson.setId(1L);
        existing.setLesson(oldLesson);

        AssignmentRequest request = new AssignmentRequest();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setDueDate(LocalDate.of(2025, 12, 31));
        request.setMaxScore(20);
        request.setLessonId(2L);

        Lesson newLesson = new Lesson();
        newLesson.setId(2L);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2L)).thenReturn(Optional.of(newLesson));
        when(assignmentRepository.save(existing)).thenReturn(existing);

        Assignment updated = assignmentService.updateAssignment(1L, request);

        assertEquals("New title", updated.getTitle());
        assertEquals("New description", updated.getDescription());
        assertEquals(LocalDate.of(2025, 12, 31), updated.getDueDate());
        assertEquals(20, updated.getMaxScore());
        assertEquals(newLesson, updated.getLesson());
    }

    @Test
    public void testUpdateAssignment_NotFound() {
        AssignmentRequest request = new AssignmentRequest();

        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> assignmentService.updateAssignment(1L, request));
    }

    @Test
    public void testUpdateAssignment_LessonNotFound() {
        Assignment existing = new Assignment();

        AssignmentRequest request = new AssignmentRequest();
        request.setLessonId(2L);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> assignmentService.updateAssignment(1L, request));
    }

    @Test
    public void testDeleteAssignment_Success() {
        Assignment assignment = new Assignment();
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        doNothing().when(assignmentRepository).delete(assignment);

        assignmentService.deleteAssignment(1L);

        verify(assignmentRepository).delete(assignment);
    }

    @Test
    public void testDeleteAssignment_NotFound() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> assignmentService.deleteAssignment(1L));
    }
}
