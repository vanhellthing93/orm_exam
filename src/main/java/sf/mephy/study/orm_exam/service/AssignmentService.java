package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.AssignmentRequest;
import sf.mephy.study.orm_exam.entity.Assignment;
import sf.mephy.study.orm_exam.entity.Lesson;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.AssignmentRepository;
import sf.mephy.study.orm_exam.repository.LessonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + id + " not found"));
    }

    public Assignment createAssignment(Assignment assignment) {
        Long lessonId = assignment.getLesson().getId();
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + lessonId + " not found"));
        assignment.setLesson(lesson);
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Long id, AssignmentRequest assignmentRequest) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + id + " not found"));

        if (assignmentRequest.getTitle() != null) {
            existingAssignment.setTitle(assignmentRequest.getTitle());
        }

        if (assignmentRequest.getDescription() != null) {
            existingAssignment.setDescription(assignmentRequest.getDescription());
        }

        if (assignmentRequest.getDueDate() != null) {
            existingAssignment.setDueDate(assignmentRequest.getDueDate());
        }

        if (assignmentRequest.getMaxScore() != null) {
            existingAssignment.setMaxScore(assignmentRequest.getMaxScore());
        }

        if (assignmentRequest.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(assignmentRequest.getLessonId())
                    .orElseThrow(() -> new EntityNotFoundException("Lesson with id " + assignmentRequest.getLessonId() + " not found"));
            existingAssignment.setLesson(lesson);
        }

        return assignmentRepository.save(existingAssignment);
    }

    public void deleteAssignment(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment with id " + id + " not found"));
        assignmentRepository.delete(assignment);
    }
}
