package es.urjc.daw04.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.dto.CategoryDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    List<CategoryDTO> toDTOs(Collection<Category> categories);

    Category toDomain(CategoryDTO categoryDTO);

}
