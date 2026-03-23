package es.urjc.daw04.controllers.rest;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import java.net.URI;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.urjc.daw04.model.dto.OrderDTO;
import es.urjc.daw04.model.User;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.service.UserService;
import es.urjc.daw04.service.OrderService;
import es.urjc.daw04.model.mapper.OrderMapper;
import es.urjc.daw04.model.Cart;
import es.urjc.daw04.service.CartService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CartService cartService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getOrders(Principal principal, Pageable pageable) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<User> user = userService.findByName(principal.getName());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Page<Order> orders = orderService.findByUserPaged(user.get(), pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderDTO> dtoPage = orders.map(orderMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Order order = orderOpt.get();

        if (!order.getUser().getName().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findByName(principal.getName()).orElseThrow();

        Cart cart = cartService.getUserCart(user);

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Order order = orderService.saveOrderFromCart(cart, user);

        cartService.clearUserCart(user);

        OrderDTO orderDTO = orderMapper.toDTO(order);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(orderDTO.id()).toUri();

        return ResponseEntity.created(location).body(orderDTO);
    }
}
