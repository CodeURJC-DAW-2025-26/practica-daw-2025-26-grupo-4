package es.urjc.daw04.model.mapper;

import org.mapstruct.Mapper;

import es.urjc.daw04.model.Cart;
import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.dto.CartDTO;
import es.urjc.daw04.model.dto.CartItemDTO;

@Mapper(componentModel = "spring", uses = {ProductMapper.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface CartMapper {

    CartDTO toDTO(Cart cart);

    CartItemDTO toDTO(CartItem cartItem);
}
