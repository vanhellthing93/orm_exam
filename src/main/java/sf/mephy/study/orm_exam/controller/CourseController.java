package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.dto.response.CourseResponse;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.mapper.CourseMapper;
import sf.mephy.study.orm_exam.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return courseMapper.toResponse(course);
    }

    @PostMapping
    public CourseResponse createCourse(@RequestBody CourseRequest courseRequest) {
        Course course = courseMapper.toEntity(courseRequest);
        Course createdCourse = courseService.createCourse(course);
        return courseMapper.toResponse(createdCourse);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id, @RequestBody CourseRequest courseRequest) {
        Course updatedCourse = courseService.updateCourse(id, courseRequest);
        return courseMapper.toResponse(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }


}
