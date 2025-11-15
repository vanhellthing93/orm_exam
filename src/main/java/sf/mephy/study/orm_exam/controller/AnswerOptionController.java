package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.AnswerOptionRequest;
import sf.mephy.study.orm_exam.dto.response.AnswerOptionResponse;
import sf.mephy.study.orm_exam.entity.AnswerOption;
import sf.mephy.study.orm_exam.mapper.AnswerOptionMapper;
import sf.mephy.study.orm_exam.service.AnswerOptionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/answer-options")
@RequiredArgsConstructor
public class AnswerOptionController {
    private final AnswerOptionService answerOptionService;
    private final AnswerOptionMapper answerOptionMapper;

    @GetMapping
    public List<AnswerOptionResponse> getAllAnswerOptions() {
        return answerOptionService.getAllAnswerOptions().stream()
                .map(answerOptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AnswerOptionResponse getAnswerOptionById(@PathVariable Long id) {
        AnswerOption answerOption = answerOptionService.getAnswerOptionById(id);
        return answerOptionMapper.toResponse(answerOption);
    }

    @PostMapping
    public AnswerOptionResponse createAnswerOption(@RequestBody AnswerOptionRequest answerOptionRequest) {
        AnswerOption answerOption = answerOptionMapper.toEntity(answerOptionRequest);
        AnswerOption createdAnswerOption = answerOptionService.createAnswerOption(answerOption);
        return answerOptionMapper.toResponse(createdAnswerOption);
    }
}
