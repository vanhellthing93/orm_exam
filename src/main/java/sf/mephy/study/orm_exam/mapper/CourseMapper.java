package sf.mephy.study.orm_exam.mapper;

import org.mapstruct.*;
import sf.mephy.study.orm_exam.dto.nested.CategoryInfo;
import sf.mephy.study.orm_exam.dto.nested.UserInfo;
import sf.mephy.study.orm_exam.dto.request.CourseRequest;
import sf.mephy.study.orm_exam.dto.response.CourseResponse;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.entity.Course;
import sf.mephy.study.orm_exam.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, CategoryMapper.class})
public interface CourseMapper {

    @Mapping(target = "teacher", source = "teacherId", qualifiedByName = "teacherIdToUser")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "categoryIdToCategory")
    Course toEntity(CourseRequest request);

    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "userToTeacherInfo")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToCategoryInfo")
    CourseResponse toResponse(Course course);

    @Named("userToTeacherInfo")
    default UserInfo userToTeacherInfo(User user) {
        if (user == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setName(user.getName());
        return userInfo;
    }

    @Named("teacherIdToUser")
    default User teacherIdToUser(Long teacherId) {
        if (teacherId == null) {
            return null;
        }
        User user = new User();
        user.setId(teacherId);
        return user;
    }

    @Named("categoryToCategoryInfo")
    default CategoryInfo categoryToCategoryInfo(Category category) {
        if (category == null) {
            return null;
        }
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setId(category.getId());
        categoryInfo.setName(category.getName());
        return categoryInfo;
    }

    @Named("categoryIdToCategory")
    default Category categoryIdToCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
