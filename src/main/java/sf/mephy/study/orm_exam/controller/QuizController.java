package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.QuizRequest;
import sf.mephy.study.orm_exam.dto.response.QuizResponse;
import sf.mephy.study.orm_exam.entity.Quiz;
import sf.mephy.study.orm_exam.mapper.QuizMapper;
import sf.mephy.study.orm_exam.service.QuizService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final QuizMapper quizMapper;

    @GetMapping
    public List<QuizResponse> getAllQuizzes() {
        return quizService.getAllQuizzes().stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public QuizResponse getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        return quizMapper.toResponse(quiz);
    }

    @PostMapping
    public QuizResponse createQuiz(@RequestBody QuizRequest quizRequest) {
        Quiz quiz = quizMapper.toEntity(quizRequest);
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return quizMapper.toResponse(createdQuiz);
    }
}
