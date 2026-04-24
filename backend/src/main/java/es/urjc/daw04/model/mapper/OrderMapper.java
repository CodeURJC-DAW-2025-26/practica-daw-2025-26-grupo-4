package es.urjc.daw04.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import es.urjc.daw04.model.Image;
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
    @Mapping(target = "imageUrl", expression = "java(resolveMainImageUrl(item))")
    @Mapping(target = "canReview", expression = "java(isDelivered(item))")
    @Mapping(target = "hasReview", expression = "java(checkIfHasReview(item))")
    public abstract OrderItemDTO toItemDTO(CartItem item);

    protected String resolveMainImageUrl(CartItem item) {
        if (item == null || item.getProduct() == null)
            return null;
        List<Image> images = item.getProduct().getImages();
        if (images == null || images.isEmpty())
            return null;
        Long id = images.get(0).getId();
        return id == null ? null : "/api/v1/images/" + id + "/media";
    }

    protected boolean isDelivered(CartItem item) {
        if (item.getOrder() == null) {
            return false;
        }
        return EnumStatus.DELIVERED.equals(item.getOrder().getStatus());
    }

    protected boolean checkIfHasReview(CartItem item) {
        if (item == null ||
                item.getProduct() == null ||
                item.getOrder() == null ||
                item.getOrder().getUser() == null) {
            return false;
        }

        return reviewService.findByProductIdAndUserId(item.getProduct().getId(),
                item.getOrder().getUser().getId()) != null;
    }
}
