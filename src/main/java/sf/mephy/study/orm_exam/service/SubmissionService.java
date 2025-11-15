package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + id + " not found"));
    }

    public List<Submission> getSubmissionsByAssignmentId(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudentId(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Submission submitAssignment(Long assignmentId, Long studentId, String content) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + assignmentId + " not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + studentId + " not found"));

        boolean alreadySubmitted = submissionRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId);
        if (alreadySubmitted) {
            throw new DuplicateEntityException("Student has already submitted a solution for this assignment.");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public Submission updateSubmission(Long id, SubmissionRequest submissionRequest) {
        Submission existingSubmission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + id + " not found"));

        if (submissionRequest.getContent() != null) {
            existingSubmission.setContent(submissionRequest.getContent());
        }

        if (submissionRequest.getScore() != null) {
            existingSubmission.setScore(submissionRequest.getScore());
        }

        if (submissionRequest.getFeedback() != null) {
            existingSubmission.setFeedback(submissionRequest.getFeedback());
        }

        if (submissionRequest.getAssignmentId() != null) {
            Assignment assignment = assignmentRepository.findById(submissionRequest.getAssignmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + submissionRequest.getAssignmentId() + " not found"));
            existingSubmission.setAssignment(assignment);
        }

        if (submissionRequest.getStudentId() != null) {
            User student = userRepository.findById(submissionRequest.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + submissionRequest.getStudentId() + " not found"));
            existingSubmission.setStudent(student);
        }

        return submissionRepository.save(existingSubmission);
    }

    public void deleteSubmission(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission with id " + id + " not found"));
        submissionRepository.delete(submission);
    }
}
