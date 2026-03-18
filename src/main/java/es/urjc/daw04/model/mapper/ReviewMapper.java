package es.urjc.daw04.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.urjc.daw04.model.Review;
import es.urjc.daw04.model.dto.ProductReviewDTO;
import es.urjc.daw04.model.dto.ReviewDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "product.id", target = "productId")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    ProductReviewDTO toProductReviewDTO(Review review);

    List<ProductReviewDTO> toProductReviewDTOs(Collection<Review> reviews);

    @Mapping(target = "product", ignore = true)
    Review toDomain(ReviewDTO reviewDTO);

}
