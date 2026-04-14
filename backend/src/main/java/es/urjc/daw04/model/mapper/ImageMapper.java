package es.urjc.daw04.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.urjc.daw04.model.Image;
import es.urjc.daw04.model.dto.ImageDTO;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "url", source = "id", qualifiedByName = "idToUrl")
    ImageDTO toDTO(Image image);

    @Named("idToUrl")
    default String idToUrl(Long id) {
        if (id == null) return null;
        return "/api/v1/images/" + id + "/media";
    }
}
