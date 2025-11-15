package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.dto.response.CourseResponse;
import sf.mephy.study.orm_exam.dto.response.UserResponse;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.mapper.CourseMapper;
import sf.mephy.study.orm_exam.mapper.UserMapper;
import sf.mephy.study.orm_exam.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

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

    @GetMapping("/user/{userId}")
    public List<CourseResponse> getCoursesByUserId(@PathVariable Long userId) {
        List<Course> courses = courseService.getCoursesByUserId(userId);
        return courses.stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseId}/students")
    public List<UserResponse> getStudentsByCourseId(@PathVariable Long courseId) {
        List<User> students = courseService.getStudentsByCourseId(courseId);
        return students.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
