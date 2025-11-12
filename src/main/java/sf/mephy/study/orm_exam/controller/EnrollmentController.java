package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.response.EnrollmentResponse;
import sf.mephy.study.orm_exam.entity.Enrollment;
import sf.mephy.study.orm_exam.mapper.EnrollmentMapper;
import sf.mephy.study.orm_exam.service.EnrollmentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    @GetMapping
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentService.getAllEnrollments().stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollmentById(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        return enrollmentMapper.toResponse(enrollment);
    }

    @PostMapping("/enroll")
    public EnrollmentResponse enrollUserToCourse(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        Enrollment enrollment = enrollmentService.enrollUserToCourse(userId, courseId);
        return enrollmentMapper.toResponse(enrollment);
    }
}
