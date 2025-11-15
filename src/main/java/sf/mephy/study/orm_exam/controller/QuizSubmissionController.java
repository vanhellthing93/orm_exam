package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.QuizSubmissionRequest;
import sf.mephy.study.orm_exam.dto.response.QuizSubmissionResponse;
import sf.mephy.study.orm_exam.entity.QuizSubmission;
import sf.mephy.study.orm_exam.mapper.QuizSubmissionMapper;
import sf.mephy.study.orm_exam.service.QuizSubmissionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quiz-submissions")
@RequiredArgsConstructor
public class QuizSubmissionController {
    private final QuizSubmissionService quizSubmissionService;
    private final QuizSubmissionMapper quizSubmissionMapper;

    @GetMapping
    public List<QuizSubmissionResponse> getAllQuizSubmissions() {
        return quizSubmissionService.getAllQuizSubmissions().stream()
                .map(quizSubmissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public QuizSubmissionResponse getQuizSubmissionById(@PathVariable Long id) {
        QuizSubmission quizSubmission = quizSubmissionService.getQuizSubmissionById(id);
        return quizSubmissionMapper.toResponse(quizSubmission);
    }

    @PostMapping
    public QuizSubmissionResponse createQuizSubmission(@RequestBody QuizSubmissionRequest quizSubmissionRequest) {
        QuizSubmission quizSubmission = quizSubmissionMapper.toEntity(quizSubmissionRequest);
        QuizSubmission createdQuizSubmission = quizSubmissionService.createQuizSubmission(quizSubmission);
        return quizSubmissionMapper.toResponse(createdQuizSubmission);
    }

    @PostMapping("/submit")
    public QuizSubmissionResponse submitQuiz(
            @RequestParam Long quizId,
            @RequestParam Long studentId,
            @RequestParam Integer score) {
        QuizSubmission quizSubmission = quizSubmissionService.submitQuiz(quizId, studentId, score);
        return quizSubmissionMapper.toResponse(quizSubmission);
    }
}
