package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import sf.mephy.study.orm_exam.dto.request.CategoryRequest;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(new Category(), new Category()));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
        verify(categoryRepository).findAll();
    }

    @Test
    public void testGetCategoryById_Success() {
        Category category = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    public void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    public void testCreateCategory_Success() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenReturn(category);

        Category created = categoryService.createCategory(category);

        assertNotNull(created);
        verify(categoryRepository).save(category);
    }

    @Test
    public void testCreateCategory_Duplicate() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEntityException.class, () -> categoryService.createCategory(category));
    }

    @Test
    public void testUpdateCategory_Success() {
        Category existing = new Category();
        existing.setName("Old Name");

        CategoryRequest request = new CategoryRequest();
        request.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category updated = categoryService.updateCategory(1L, request);

        assertEquals("New Name", updated.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existing);
    }

    @Test
    public void testUpdateCategory_NotFound() {
        CategoryRequest request = new CategoryRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(1L, request));
    }

    @Test
    public void testDeleteCategory_Success() {
        Category category = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    public void testDeleteCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }
}
