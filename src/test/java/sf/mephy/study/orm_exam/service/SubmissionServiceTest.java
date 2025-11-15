package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sf.mephy.study.orm_exam.dto.request.SubmissionRequest;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Submission;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AssignmentRepository;
import sf.mephy.study.orm_exam.repository.SubmissionRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    public void testGetAllSubmissions() {
        when(submissionRepository.findAll()).thenReturn(Arrays.asList(new Submission(), new Submission()));

        List<Submission> submissions = submissionService.getAllSubmissions();

        assertEquals(2, submissions.size());
        verify(submissionRepository).findAll();
    }

    @Test
    public void testGetSubmissionById_Success() {
        Submission submission = new Submission();
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        Submission result = submissionService.getSubmissionById(1L);

        assertNotNull(result);
        verify(submissionRepository).findById(1L);
    }

    @Test
    public void testGetSubmissionById_NotFound() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.getSubmissionById(1L));
    }

    @Test
    public void testGetSubmissionsByAssignmentId() {
        List<Submission> list = Arrays.asList(new Submission(), new Submission());
        when(submissionRepository.findByAssignmentId(1L)).thenReturn(list);

        List<Submission> result = submissionService.getSubmissionsByAssignmentId(1L);

        assertEquals(2, result.size());
        verify(submissionRepository).findByAssignmentId(1L);
    }

    @Test
    public void testGetSubmissionsByStudentId() {
        List<Submission> list = Arrays.asList(new Submission(), new Submission());
        when(submissionRepository.findByStudentId(1L)).thenReturn(list);

        List<Submission> result = submissionService.getSubmissionsByStudentId(1L);

        assertEquals(2, result.size());
        verify(submissionRepository).findByStudentId(1L);
    }

    @Test
    public void testCreateSubmission() {
        Submission submission = new Submission();

        when(submissionRepository.save(submission)).thenReturn(submission);

        Submission created = submissionService.createSubmission(submission);

        assertNotNull(created);
        verify(submissionRepository).save(submission);
    }

    @Test
    public void testSubmitAssignment_Success() {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        User student = new User();
        student.setId(2L);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(submissionRepository.existsByStudentIdAndAssignmentId(2L, 1L)).thenReturn(false);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArgument(0));

        Submission submission = submissionService.submitAssignment(1L, 2L, "Answer content");

        assertNotNull(submission);
        assertEquals(assignment, submission.getAssignment());
        assertEquals(student, submission.getStudent());
        assertEquals("Answer content", submission.getContent());
        assertNotNull(submission.getSubmittedAt());

        verify(assignmentRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(submissionRepository).existsByStudentIdAndAssignmentId(2L, 1L);
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    public void testSubmitAssignment_AssignmentNotFound() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.submitAssignment(1L, 2L, "Answer"));
    }

    @Test
    public void testSubmitAssignment_StudentNotFound() {
        Assignment assignment = new Assignment();
        assignment.setId(1L);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.submitAssignment(1L, 2L, "Answer"));
    }

    @Test
    public void testSubmitAssignment_AlreadySubmitted() {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        User student = new User();
        student.setId(2L);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));
        when(submissionRepository.existsByStudentIdAndAssignmentId(2L, 1L)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> submissionService.submitAssignment(1L, 2L, "Answer"));
    }

    @Test
    public void testUpdateSubmission_Success() {
        Submission existing = new Submission();
        existing.setContent("Old content");
        existing.setScore(50);
        existing.setFeedback("Old feedback");

        Assignment oldAssignment = new Assignment();
        oldAssignment.setId(1L);
        existing.setAssignment(oldAssignment);

        User oldStudent = new User();
        oldStudent.setId(1L);
        existing.setStudent(oldStudent);

        SubmissionRequest request = new SubmissionRequest();
        request.setContent("New content");
        request.setScore(80);
        request.setFeedback("New feedback");
        request.setAssignmentId(2L);
        request.setStudentId(3L);

        Assignment newAssignment = new Assignment();
        newAssignment.setId(2L);
        User newStudent = new User();
        newStudent.setId(3L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(assignmentRepository.findById(2L)).thenReturn(Optional.of(newAssignment));
        when(userRepository.findById(3L)).thenReturn(Optional.of(newStudent));
        when(submissionRepository.save(existing)).thenReturn(existing);

        Submission updated = submissionService.updateSubmission(1L, request);

        assertEquals("New content", updated.getContent());
        assertEquals(80, updated.getScore());
        assertEquals("New feedback", updated.getFeedback());
        assertEquals(newAssignment, updated.getAssignment());
        assertEquals(newStudent, updated.getStudent());
    }

    @Test
    public void testUpdateSubmission_NotFound() {
        SubmissionRequest request = new SubmissionRequest();
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.updateSubmission(1L, request));
    }

    @Test
    public void testUpdateSubmission_AssignmentNotFound() {
        Submission existing = new Submission();
        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(2L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(assignmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.updateSubmission(1L, request));
    }

    @Test
    public void testUpdateSubmission_StudentNotFound() {
        Submission existing = new Submission();
        SubmissionRequest request = new SubmissionRequest();
        request.setStudentId(3L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.updateSubmission(1L, request));
    }

    @Test
    public void testDeleteSubmission_Success() {
        Submission submission = new Submission();
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        doNothing().when(submissionRepository).delete(submission);

        submissionService.deleteSubmission(1L);

        verify(submissionRepository).delete(submission);
    }

    @Test
    public void testDeleteSubmission_NotFound() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionService.deleteSubmission(1L));
    }
}
