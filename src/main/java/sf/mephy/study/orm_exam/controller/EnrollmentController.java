package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.entity.Enrollment;
import sf.mephy.study.orm_exam.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/{id}")
    public Enrollment getEnrollmentById(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id);
    }

    @PostMapping("/enroll")
    public Enrollment enrollUserToCourse(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        return enrollmentService.enrollUserToCourse(userId, courseId);
    }
}
