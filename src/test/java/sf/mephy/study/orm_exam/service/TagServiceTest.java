package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import sf.mephy.study.orm_exam.dto.request.TagRequest;
import sf.mephy.study.orm_exam.entity.Tag;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.TagRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    public void testGetAllTags() {
        when(tagRepository.findAll()).thenReturn(Arrays.asList(new Tag(), new Tag()));

        List<Tag> tags = tagService.getAllTags();

        assertEquals(2, tags.size());
        verify(tagRepository).findAll();
    }

    @Test
    public void testGetTagById_Success() {
        Tag tag = new Tag();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag result = tagService.getTagById(1L);

        assertNotNull(result);
        verify(tagRepository).findById(1L);
    }

    @Test
    public void testGetTagById_NotFound() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(1L));
    }

    @Test
    public void testCreateTag_Success() {
        Tag tag = new Tag();
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag created = tagService.createTag(tag);

        assertNotNull(created);
        verify(tagRepository).save(tag);
    }

    @Test
    public void testCreateTag_Duplicate() {
        Tag tag = new Tag();
        when(tagRepository.save(tag)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEntityException.class, () -> tagService.createTag(tag));
    }

    @Test
    public void testUpdateTag_Success() {
        Tag existing = new Tag();
        existing.setName("Old name");

        TagRequest request = new TagRequest();
        request.setName("New name");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(existing);

        Tag updated = tagService.updateTag(1L, request);

        assertEquals("New name", updated.getName());
        verify(tagRepository).findById(1L);
        verify(tagRepository).save(existing);
    }

    @Test
    public void testUpdateTag_NotFound() {
        TagRequest request = new TagRequest();
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.updateTag(1L, request));
    }

    @Test
    public void testDeleteTag_Success() {
        Tag tag = new Tag();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        tagService.deleteTag(1L);

        verify(tagRepository).delete(tag);
    }

    @Test
    public void testDeleteTag_NotFound() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.deleteTag(1L));
    }
}
