package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import sf.mephy.study.orm_exam.dto.request.CategoryRequest;
import sf.mephy.study.orm_exam.dto.response.CategoryResponse;
import sf.mephy.study.orm_exam.entity.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);
}
