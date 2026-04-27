package es.urjc.daw04.controllers.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.AdminCategoryRequestDTO;
import es.urjc.daw04.model.dto.AdminStatsResponseDTO;
import es.urjc.daw04.model.dto.AdminUserBanRequestDTO;
import es.urjc.daw04.model.dto.AdminUserDTO;
import es.urjc.daw04.model.dto.AdminUserUpdateRequestDTO;
import es.urjc.daw04.model.dto.ChartSeriesDTO;
import es.urjc.daw04.model.dto.ErrorResponseDTO;
import es.urjc.daw04.model.dto.PagedResponseDTO;
import es.urjc.daw04.model.dto.SuccessResponseDTO;
import es.urjc.daw04.model.mapper.CategoryMapper;
import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.OrderService;
import es.urjc.daw04.service.ReviewService;
import es.urjc.daw04.service.UserService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/users")
    public PagedResponseDTO<AdminUserDTO> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<User> usersPage = userService.findAllPaged(page, size);
        List<AdminUserDTO> users = usersPage.getContent().stream().map(this::toAdminUserDTO).toList();

        return new PagedResponseDTO<>(
                users,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.hasNext());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUserUpdateRequestDTO request) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponseDTO("Usuario no encontrado"));
        }

        if (user.isAdmin()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("No se permite editar administradores"));
        }

        if (request.username() != null && !request.username().isBlank()) {
            user.setName(request.username().trim());
        }

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email().trim());
        }

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }

        if (request.birthDate() != null && !request.birthDate().isBlank()) {
            try {
                user.setBirthDate(LocalDate.parse(request.birthDate()));
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(new ErrorResponseDTO("Fecha de nacimiento inválida"));
            }
        }

        user.setShippingAddress(request.shippingAddress() != null ? request.shippingAddress().trim() : "");

        userService.save(user);
        return ResponseEntity.ok(toAdminUserDTO(user));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<?> updateBanStatus(@PathVariable Long id, @RequestBody AdminUserBanRequestDTO request) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponseDTO("Usuario no encontrado"));
        }

        if (user.isAdmin()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("No se permite modificar administradores"));
        }

        if (request == null || request.status() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("El campo status es obligatorio"));
        }

        String status = request.status().trim().toLowerCase();
        if (!"ban".equals(status) && !"unban".equals(status)) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Status inválido. Usa 'ban' o 'unban'"));
        }

        user.setBanned("ban".equals(status));
        userService.save(user);
        return ResponseEntity.ok(toAdminUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(new ErrorResponseDTO("Usuario no encontrado"));
        }

        if (user.isAdmin()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("No se permite eliminar administradores"));
        }

        userService.deleteById(id);
        return ResponseEntity.ok(new SuccessResponseDTO("Usuario eliminado correctamente"));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(categoryService.findAll().stream().map(categoryMapper::toDTO).toList());
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody AdminCategoryRequestDTO request) {
        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("El nombre de la categoría es obligatorio"));
        }

        String name = request.name().trim();
        String slug = slugify(name);
        Category category = new Category(name, slug, request.icon());
        categoryService.save(category);

        return ResponseEntity.status(201).body(categoryMapper.toDTO(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody AdminCategoryRequestDTO request) {
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            return ResponseEntity.status(404).body(new ErrorResponseDTO("Categoría no encontrada"));
        }

        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("El nombre de la categoría es obligatorio"));
        }

        String name = request.name().trim();
        category.setName(name);
        category.setSlug(slugify(name));
        category.setIcon(request.icon());
        categoryService.save(category);

        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
            return ResponseEntity.ok(new SuccessResponseDTO("Categoría eliminada correctamente"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    "No se puede eliminar la categoría porque tiene productos asociados o ocurrió un error"));
        }
    }

    @GetMapping("/stats")
    public AdminStatsResponseDTO getStats() {
        return new AdminStatsResponseDTO(
                toChartSeries(orderService.getSalesByCategory()),
                toChartSeries(orderService.getSalesByTag()),
                toChartSeriesWithMonths(orderService.getMonthlySales()),
                toChartSeriesWithMonths(orderService.getOrdersCountByMonth()),
                toChartSeriesWithMonths(reviewService.getReviewCountByMonth()));
    }

    private AdminUserDTO toAdminUserDTO(User user) {
        return new AdminUserDTO(
                user.getId(),
                user.getName(),
                user.getFullName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getShippingAddress(),
                user.getRoles(),
                user.isBanned(),
                user.isAdmin());
    }

    private ChartSeriesDTO toChartSeries(List<Object[]> data) {
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : data) {
            if (row[0] != null) {
                labels.add(row[0].toString());
                values.add(((Number) row[1]).doubleValue());
            }
        }

        return new ChartSeriesDTO(labels, values);
    }

    private ChartSeriesDTO toChartSeriesWithMonths(List<Object[]> data) {
        String[] months = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        double[] monthValues = new double[12];

        for (Object[] row : data) {
            if (row[0] != null) {
                int month = ((Number) row[0]).intValue();
                if (month >= 1 && month <= 12) {
                    monthValues[month - 1] = ((Number) row[1]).doubleValue();
                }
            }
        }

        for (int i = 0; i < 12; i++) {
            labels.add(months[i]);
            values.add(monthValues[i]);
        }

        return new ChartSeriesDTO(labels, values);
    }

    private String slugify(String value) {
        return value.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
    }
}
