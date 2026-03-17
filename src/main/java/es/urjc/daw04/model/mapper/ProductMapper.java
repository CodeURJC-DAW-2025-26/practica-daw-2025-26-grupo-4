package es.urjc.daw04.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.dto.ProductDTO;
import es.urjc.daw04.model.dto.ProductSummaryDTO;

@Mapper(componentModel = "spring", uses = {ReviewMapper.class, CategoryMapper.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    ProductSummaryDTO toSummaryDTO(Product product);

    List<ProductDTO> toDTOs(Collection<Product> products);

    Product toDomain(ProductDTO productDTO);

}
