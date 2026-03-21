package es.urjc.daw04.model.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.dto.OrderDTO;
import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.dto.OrderItemDTO;
import es.urjc.daw04.model.EnumStatus;
import es.urjc.daw04.service.ReviewService;

@Mapper(componentModel = "spring", uses = { ImageMapper.class })
public abstract class OrderMapper {

    @Autowired
    protected ReviewService reviewService;

    @Mapping(target = "orderNumber", expression = "java(String.format(\"ORD-%06d\", order.getId()))")
    @Mapping(source = "orderDate", target = "date", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "items", source = "items")
    public abstract OrderDTO toDTO(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.mainImage", target = "imageUrl")
    @Mapping(target = "canReview", expression = "java(isDelivered(item))")
    @Mapping(target = "hasReview", expression = "java(checkIfHasReview(item))")
    public abstract OrderItemDTO toItemDTO(CartItem item);

    protected boolean isDelivered(CartItem item) {
        if (item.getOrder() == null) { return false; }
        return EnumStatus.DELIVERED.equals(item.getOrder().getStatus());
    }

    protected boolean checkIfHasReview(CartItem item) {
        if (item == null ||
            item.getProduct() == null ||
            item.getOrder() == null ||
            item.getOrder().getUser() == null) {
                return false; }

        return reviewService.findByProductIdAndUserId( item.getProduct().getId(), item.getOrder().getUser().getId()) != null;
    }
}
