package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.CourseReviewRequest;
import sf.mephy.study.orm_exam.dto.response.CourseReviewResponse;
import sf.mephy.study.orm_exam.entity.CourseReview;
import sf.mephy.study.orm_exam.mapper.CourseReviewMapper;
import sf.mephy.study.orm_exam.service.CourseReviewService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/course-reviews")
@RequiredArgsConstructor
public class CourseReviewController {
    private final CourseReviewService courseReviewService;
    private final CourseReviewMapper courseReviewMapper;

    @GetMapping
    public List<CourseReviewResponse> getAllCourseReviews() {
        return courseReviewService.getAllCourseReviews().stream()
                .map(courseReviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CourseReviewResponse getCourseReviewById(@PathVariable Long id) {
        CourseReview courseReview = courseReviewService.getCourseReviewById(id);
        return courseReviewMapper.toResponse(courseReview);
    }

    @PostMapping
    public CourseReviewResponse createCourseReview(@RequestBody CourseReviewRequest courseReviewRequest) {
        CourseReview courseReview = courseReviewMapper.toEntity(courseReviewRequest);
        CourseReview createdCourseReview = courseReviewService.createCourseReview(courseReview);
        return courseReviewMapper.toResponse(createdCourseReview);
    }

    @PostMapping("/{courseId}/{studentId}")
    public CourseReviewResponse createCourseReview(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestBody CourseReviewRequest courseReviewRequest) {
        CourseReview courseReview = courseReviewMapper.toEntity(courseReviewRequest);
        CourseReview createdCourseReview = courseReviewService.createCourseReview(courseId, studentId, courseReview);
        return courseReviewMapper.toResponse(createdCourseReview);
    }

    @PutMapping("/{id}")
    public CourseReviewResponse updateCourseReview(@PathVariable Long id, @RequestBody CourseReviewRequest courseReviewRequest) {
        CourseReview updatedReview = courseReviewService.updateCourseReview(id, courseReviewRequest);
        return courseReviewMapper.toResponse(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseReview(@PathVariable Long id) {
        courseReviewService.deleteCourseReview(id);
        return ResponseEntity.noContent().build();
    }
}
