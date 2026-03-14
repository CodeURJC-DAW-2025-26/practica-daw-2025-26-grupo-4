package es.urjc.daw04.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.dto.ReviewDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReviewMapper {

    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    Review toDomain(ReviewDTO reviewDTO);

}
