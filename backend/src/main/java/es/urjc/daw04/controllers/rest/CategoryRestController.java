package es.urjc.daw04.controllers.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.dto.CategoryDTO;
import es.urjc.daw04.model.mapper.CategoryMapper;
import es.urjc.daw04.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDTO> getCategories() {
        return categoryMapper.toDTOs(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@PathVariable long id) {
        Category category = categoryService.findById(id).orElse(null);
        return categoryMapper.toDTO(category);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        String name = categoryDTO.name();
        String slug = name.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        Category category = new Category(name, slug, categoryDTO.icon());
        categoryService.save(category);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(category.getId()).toUri();

        return ResponseEntity.created(location).body(categoryMapper.toDTO(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable long id, @RequestBody CategoryDTO categoryDTO) {
        Optional<Category> optionalCategory = categoryService.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(categoryDTO.name());
            category.setSlug(categoryDTO.name().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", ""));
            category.setIcon(categoryDTO.icon());
            categoryService.save(category);
            return ResponseEntity.ok(categoryMapper.toDTO(category));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        try {
            categoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Manejamos la excepción por si la categoría tiene productos asociados
            return ResponseEntity.badRequest().build();
        }
    }
}
