package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.TagRequest;
import sf.mephy.study.orm_exam.dto.response.TagResponse;
import sf.mephy.study.orm_exam.entity.Tag;
import sf.mephy.study.orm_exam.mapper.TagMapper;
import sf.mephy.study.orm_exam.service.TagService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public List<TagResponse> getAllTags() {
        return tagService.getAllTags().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagResponse getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        return tagMapper.toResponse(tag);
    }

    @PostMapping
    public TagResponse createTag(@RequestBody TagRequest tagRequest) {
        Tag tag = tagMapper.toEntity(tagRequest);
        Tag createdTag = tagService.createTag(tag);
        return tagMapper.toResponse(createdTag);
    }
}
