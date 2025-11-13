package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Submission;
import sf.mephy.study.orm_exam.entity.User;
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

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Submission submitAssignment(Long assignmentId, Long studentId, String content) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + assignmentId + " not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + studentId + " not found"));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }
}
