package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.AssignmentRequest;
import sf.mephy.study.orm_exam.dto.response.AssignmentResponse;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.mapper.AssignmentMapper;
import sf.mephy.study.orm_exam.service.AssignmentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssignmentMapper assignmentMapper;

    @GetMapping
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentService.getAllAssignments().stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AssignmentResponse getAssignmentById(@PathVariable Long id) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        return assignmentMapper.toResponse(assignment);
    }

    @PostMapping
    public AssignmentResponse createAssignment(@RequestBody AssignmentRequest assignmentRequest) {
        Assignment assignment = assignmentMapper.toEntity(assignmentRequest);
        Assignment createdAssignment = assignmentService.createAssignment(assignment);
        return assignmentMapper.toResponse(createdAssignment);
    }

    @PutMapping("/{id}")
    public AssignmentResponse updateAssignment(@PathVariable Long id, @RequestBody AssignmentRequest assignmentRequest) {
        Assignment updatedAssignment = assignmentService.updateAssignment(id, assignmentRequest);
        return assignmentMapper.toResponse(updatedAssignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

}
