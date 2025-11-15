package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.CategoryRequest;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    }

    public Category createCategory(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntityException("Category with this name already exists.");
        }
    }

    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));

        if (categoryRequest.getName() != null) {
            existingCategory.setName(categoryRequest.getName());
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        categoryRepository.delete(category);
    }

}
