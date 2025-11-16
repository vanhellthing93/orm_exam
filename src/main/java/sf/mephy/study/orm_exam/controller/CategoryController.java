package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.CategoryRequest;
import sf.mephy.study.orm_exam.dto.response.CategoryResponse;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.mapper.CategoryMapper;
import sf.mephy.study.orm_exam.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return categoryMapper.toResponse(category);
    }

    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryRequest categoryRequest) {
        Category category = categoryMapper.toEntity(categoryRequest);
        Category createdCategory = categoryService.createCategory(category);
        return categoryMapper.toResponse(createdCategory);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return categoryMapper.toResponse(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
