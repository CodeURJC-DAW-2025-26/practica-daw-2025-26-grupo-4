package es.urjc.daw04.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import es.urjc.daw04.model.Image;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.User;
import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ImageService;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.UserService;

@Controller
public class AdminController {

    private static final int ADMIN_USERS_PAGE_SIZE = 5;
    private static final int ADMIN_PRODUCTS_PAGE_SIZE = 10;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/admin")
    public String admin(Model model) {
        Page<User> firstPage = userService.findAllPaged(0, ADMIN_USERS_PAGE_SIZE);
        List<Map<String, Object>> usersData = toUsersData(firstPage.getContent());
        model.addAttribute("users", usersData);
        model.addAttribute("hasMore", firstPage.hasNext());
        return "admin";
    }

    @GetMapping("/api/admin/users/fragment")
    public String adminUsersFragment(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<User> p = userService.findAllPaged(page, ADMIN_USERS_PAGE_SIZE);
        model.addAttribute("users", toUsersData(p.getContent()));
        return "fragments/admin-users";
    }

    private List<Map<String, Object>> toUsersData(List<User> users) {
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("fullName", u.getFullName() != null ? u.getFullName() : "");
            map.put("email", u.getEmail() != null ? u.getEmail() : "");
            map.put("roles", String.join(", ", u.getRoles()));
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String catError,
            @RequestParam(required = false) String catSuccess) {
        Page<Product> firstPage = productService.findAllPaged(0, ADMIN_PRODUCTS_PAGE_SIZE);
        model.addAttribute("products", toProductsData(firstPage.getContent()));
        model.addAttribute("hasMore", firstPage.hasNext());

        List<Map<String, Object>> categoriesData = categoryService.findAll().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("icon", c.getIcon());
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("categories", categoriesData);

        if (error != null) model.addAttribute("errorMsg", error);
        if (success != null) model.addAttribute("successMsg", "Producto creado correctamente.");

        if (catError != null) model.addAttribute("catErrorMsg", catError);
        if (catSuccess != null) model.addAttribute("catSuccessMsg", "Categoría gestionada correctamente.");

        return "admin-products";
    }

    @GetMapping("/api/admin/products/fragment")
    public String adminProductsFragment(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Product> p = productService.findAllPaged(page, ADMIN_PRODUCTS_PAGE_SIZE);
        model.addAttribute("products", toProductsData(p.getContent()));
        return "fragments/admin-products-rows";
    }

    private List<Map<String, Object>> toProductsData(List<Product> products) {
        return products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : "-");
            map.put("price", String.format("%.2f", p.getPrice()));
            return map;
        }).collect(Collectors.toList());
    }

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_MIME = List.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/avif");

    @PostMapping("/admin/products/create")
    public void createProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpServletResponse response) throws IOException {

        // --- Validación backend ---
        String validationError = null;

        List<MultipartFile> validImages = (images != null)
                ? images.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList())
                : List.of();

        if (name == null || name.isBlank()) {
            validationError = "El nombre es obligatorio.";
        } else if (name.trim().length() < 2) {
            validationError = "El nombre debe tener al menos 2 caracteres.";
        } else if (name.trim().length() > 100) {
            validationError = "El nombre no puede superar los 100 caracteres.";
        } else if (price == null || price.isBlank()) {
            validationError = "El precio es obligatorio.";
        } else {
            try {
                double priceVal = Double.parseDouble(price.replace(',', '.'));
                if (priceVal <= 0) {
                    validationError = "El precio debe ser mayor que 0.";
                } else if (priceVal > 99999) {
                    validationError = "El precio no puede superar 99.999 €.";
                } else if (description != null && description.length() > 500) {
                    validationError = "La descripción no puede superar los 500 caracteres.";
                } else if (validImages.isEmpty()) {
                    validationError = "Debes subir al menos una imagen.";
                } else {
                    for (MultipartFile f : validImages) {
                        if (f.getSize() > MAX_IMAGE_SIZE) {
                            validationError = "Cada imagen no puede superar los 5 MB.";
                            break;
                        }
                        if (f.getContentType() == null || !ALLOWED_MIME.contains(f.getContentType())) {
                            validationError = "Solo se permiten imágenes JPG, PNG, WEBP o GIF.";
                            break;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                validationError = "El precio introducido no es válido.";
            }
        }

        if (validationError != null) {
            response.sendRedirect("/admin/products?error=" + URLEncoder.encode(validationError, StandardCharsets.UTF_8));
            return;
        }

        // --- Creación ---
        double priceVal = Double.parseDouble(price.replace(',', '.'));

        List<String> tagList = (tags != null && !tags.isBlank())
                ? Arrays.stream(tags.split(",")).map(String::trim).filter(t -> !t.isEmpty()).collect(Collectors.toList())
                : List.of();

        Product product = new Product(name.trim(), priceVal,
                description != null ? description.trim() : "",
                tagList);

        if (categoryId != null) {
            categoryService.findById(categoryId).ifPresent(product::setCategory);
        }

        for (MultipartFile f : validImages) {
            Image img = imageService.createImage(f);
            product.getImages().add(img);
        }

        productService.save(product);
        response.sendRedirect("/admin/products?success=true");
    }

    @PostMapping("/admin/products/{id}/delete")
    public void deleteProduct(@PathVariable Long id, HttpServletResponse response) throws IOException {
        productService.deleteById(id);
        response.sendRedirect("/admin/products");
    }

    @PostMapping("/admin/categories/create")
    public void createCategory(@RequestParam String name, @RequestParam(required = false) String icon, HttpServletResponse response) throws IOException {
        String slug = name.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
        es.urjc.daw04.model.Category category = new es.urjc.daw04.model.Category(name, slug, icon);
        categoryService.save(category);
        response.sendRedirect("/admin/products?catSuccess=true");
    }

    @PostMapping("/admin/categories/{id}/update")
    public void updateCategory(@PathVariable Long id, @RequestParam String name, @RequestParam(required = false) String icon, HttpServletResponse response) throws IOException {
         categoryService.findById(id).ifPresent(category -> {
             category.setName(name);
             // Opcional: actualizar slug si cambia el nombre
             category.setSlug(name.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", ""));
             category.setIcon(icon);
             categoryService.save(category);
         });
         response.sendRedirect("/admin/products?catSuccess=true");
    }

    @PostMapping("/admin/categories/{id}/delete")
    public void deleteCategory(@PathVariable Long id, HttpServletResponse response) throws IOException {
        try {
            categoryService.deleteById(id);
            response.sendRedirect("/admin/products?catSuccess=true");
        } catch (Exception e) {
             String error = "No se puede eliminar la categoría porque tiene productos asociados o ocurrió un error.";
             response.sendRedirect("/admin/products?catError=" + URLEncoder.encode(error, StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/admin/users/{id}/delete")
    public void deleteUser(@PathVariable Long id, HttpServletResponse response) throws IOException {
        userService.deleteById(id);
        response.sendRedirect("/admin");
    }

}
