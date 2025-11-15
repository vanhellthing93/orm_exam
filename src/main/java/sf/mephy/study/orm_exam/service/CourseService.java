package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CategoryRepository;
import sf.mephy.study.orm_exam.repository.CourseRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course with id " + id + " not found"));
    }

    public Course createCourse(Course course) {
        Long teacherId = course.getTeacher().getId();
        Long categoryId = course.getCategory().getId();

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher with id " + teacherId + " not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));

        course.setTeacher(teacher);
        course.setCategory(category);

        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseRequest courseRequest) {
        Course existingCourse = getCourseById(id);

        // Обновляем только те поля, которые были переданы в запросе
        if (courseRequest.getTitle() != null) {
            existingCourse.setTitle(courseRequest.getTitle());
        }

        if (courseRequest.getDescription() != null) {
            existingCourse.setDescription(courseRequest.getDescription());
        }


        if (courseRequest.getTeacherId() != null) {
            User newTeacher = userRepository.findById(courseRequest.getTeacherId())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher with id " + courseRequest.getTeacherId() + " not found"));
            existingCourse.setTeacher(newTeacher);
        }

        if (courseRequest.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(courseRequest.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + courseRequest.getCategoryId() + " not found"));
            existingCourse.setCategory(newCategory);
        }

        if (courseRequest.getStartDate() != null) {
            existingCourse.setStartDate(courseRequest.getStartDate());
        }

        if (courseRequest.getDuration() != null) {
            existingCourse.setDuration(courseRequest.getDuration());
        }

        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}
