package es.urjc.daw04.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw04.model.RecommendationPack;
import es.urjc.daw04.model.dto.RecommendationPackDTO;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface RecommendationMapper {
    @Mapping(source = "formattedTotalPrice", target = "totalPrice")
    RecommendationPackDTO toDTO(RecommendationPack recommendationPack);
    
    List<RecommendationPackDTO> toDTOs(List<RecommendationPack> recommendationPacks);
}
