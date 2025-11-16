package sf.mephy.study.orm_exam.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sf.mephy.study.orm_exam.dto.request.CategoryRequest;
import sf.mephy.study.orm_exam.dto.response.CategoryResponse;
import sf.mephy.study.orm_exam.entity.Category;
import sf.mephy.study.orm_exam.repository.CategoryRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
            .withDatabaseName(System.getenv().getOrDefault("DB_NAME", "testdb"))
            .withUsername(System.getenv().getOrDefault("DB_USERNAME", "testuser"))
            .withPassword(System.getenv().getOrDefault("DB_PASSWORD", "testpass"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category existingCategory;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
        existingCategory = new Category();
        existingCategory.setName("Existing Category");
        existingCategory = categoryRepository.save(existingCategory);
    }

    @Test
    public void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].name").value(existingCategory.getName()));
    }

    @Test
    public void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", existingCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCategory.getId()))
                .andExpect(jsonPath("$.name").value(existingCategory.getName()));
    }

    @Test
    public void testCreateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("New Category");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Name");
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/categories/{id}", existingCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCategory.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", existingCategory.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categories/{id}", existingCategory.getId()))
                .andExpect(status().isNotFound());
    }
}
