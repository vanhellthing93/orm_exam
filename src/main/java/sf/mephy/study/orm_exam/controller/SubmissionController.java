package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.SubmissionContentRequest;
import sf.mephy.study.orm_exam.dto.request.SubmissionRequest;
import sf.mephy.study.orm_exam.dto.response.SubmissionResponse;
import sf.mephy.study.orm_exam.entity.Submission;
import sf.mephy.study.orm_exam.mapper.SubmissionMapper;
import sf.mephy.study.orm_exam.service.SubmissionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;

    @GetMapping
    public List<SubmissionResponse> getAllSubmissions() {
        return submissionService.getAllSubmissions().stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SubmissionResponse getSubmissionById(@PathVariable Long id) {
        Submission submission = submissionService.getSubmissionById(id);
        return submissionMapper.toResponse(submission);
    }

    @PostMapping
    public SubmissionResponse createSubmission(@RequestBody SubmissionRequest submissionRequest) {
        Submission submission = submissionMapper.toEntity(submissionRequest);
        Submission createdSubmission = submissionService.createSubmission(submission);
        return submissionMapper.toResponse(createdSubmission);
    }

    @PostMapping("/submit")
    public SubmissionResponse submitAssignment(
            @RequestParam Long assignmentId,
            @RequestParam Long studentId,
            @RequestBody SubmissionContentRequest contentRequest) {
        Submission submission = submissionService.submitAssignment(assignmentId, studentId, contentRequest.getContent());
        return submissionMapper.toResponse(submission);
    }
}
