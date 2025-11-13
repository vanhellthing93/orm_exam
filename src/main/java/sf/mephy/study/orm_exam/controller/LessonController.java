package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.LessonRequest;
import sf.mephy.study.orm_exam.dto.response.LessonResponse;
import sf.mephy.study.orm_exam.entity.Lesson;
import sf.mephy.study.orm_exam.mapper.LessonMapper;
import sf.mephy.study.orm_exam.service.LessonService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @GetMapping
    public List<LessonResponse> getAllLessons() {
        return lessonService.getAllLessons().stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public LessonResponse getLessonById(@PathVariable Long id) {
        Lesson lesson = lessonService.getLessonById(id);
        return lessonMapper.toResponse(lesson);
    }

    @PostMapping
    public LessonResponse createLesson(@RequestBody LessonRequest lessonRequest) {
        Lesson lesson = lessonMapper.toEntity(lessonRequest);
        Lesson createdLesson = lessonService.createLesson(lesson);
        return lessonMapper.toResponse(createdLesson);
    }
}
