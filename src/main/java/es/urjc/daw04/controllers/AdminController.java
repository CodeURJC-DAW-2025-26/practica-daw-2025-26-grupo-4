package es.urjc.daw04.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ImageService;
import es.urjc.daw04.service.ProductService;
import es.urjc.daw04.service.UserService;

@Controller
public class AdminController {

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
        List<Map<String, Object>> usersData = userService.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("fullName", u.getFullName() != null ? u.getFullName() : "");
            map.put("email", u.getEmail() != null ? u.getEmail() : "");
            map.put("birthDate", u.getBirthDate() != null ? u.getBirthDate().toString() : "");
            map.put("shippingAddress", u.getShippingAddress() != null ? u.getShippingAddress() : "");

            // Parsear campos de dirección
            parseAddressFields(u.getShippingAddress(), map);

            map.put("roles", String.join(", ", u.getRoles()));
            map.put("rolesList", u.getRoles());
            map.put("banned", u.isBanned());
            map.put("isAdmin", u.isAdmin());
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("users", usersData);
        return "admin";
    }

    private void parseAddressFields(String address, Map<String, Object> map) {
        // Valores por defecto
        map.put("street", "");
        map.put("additional", "");
        map.put("city", "");
        map.put("province", "");
        map.put("postalCode", "");
        map.put("country", "");
        map.put("phone", "");

        if (address == null || address.isBlank()) {
            return;
        }

        // Parsear las líneas de la dirección
        String[] lines = address.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String label = parts[0].trim();
                String value = parts.length > 1 ? parts[1].trim() : "";

                switch (label) {
                    case "Calle":
                        map.put("street", value);
                        break;
                    case "Información adicional":
                        map.put("additional", value);
                        break;
                    case "Ciudad":
                        map.put("city", value);
                        break;
                    case "Provincia":
                        map.put("province", value);
                        break;
                    case "Código Postal":
                        map.put("postalCode", value);
                        break;
                    case "País":
                        map.put("country", value);
                        break;
                    case "Teléfono":
                        map.put("phone", value);
                        break;
                }
            }
        }
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String success) {
        List<Map<String, Object>> productsData = productService.findAll().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : "-");
            map.put("price", String.format("%.2f", p.getPrice()));
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("products", productsData);

        List<Map<String, Object>> categoriesData = categoryService.findAll().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            return map;
        }).collect(Collectors.toList());
        model.addAttribute("categories", categoriesData);

        if (error != null)
            model.addAttribute("errorMsg", error);
        if (success != null)
            model.addAttribute("successMsg", "Producto creado correctamente.");

        return "admin-products";
    }

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_MIME = List.of("image/jpeg", "image/png", "image/webp", "image/gif",
            "image/avif");

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
            response.sendRedirect(
                    "/admin/products?error=" + URLEncoder.encode(validationError, StandardCharsets.UTF_8));
            return;
        }

        // --- Creación ---
        double priceVal = Double.parseDouble(price.replace(',', '.'));

        List<String> tagList = (tags != null && !tags.isBlank())
                ? Arrays.stream(tags.split(",")).map(String::trim).filter(t -> !t.isEmpty())
                        .collect(Collectors.toList())
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

    @PostMapping("/admin/users/{id}/delete")
    public void deleteUser(@PathVariable Long id, HttpServletResponse response) throws IOException {
        userService.findById(id).ifPresent(user -> {
            if (!user.isAdmin()) {
                userService.deleteById(id);
            }
        });
        response.sendRedirect("/admin");
    }

    @PostMapping("/admin/users/{id}/ban")
    public void banUser(@PathVariable Long id, HttpServletResponse response) throws IOException {
        userService.findById(id).ifPresent(user -> {
            if (!user.isAdmin()) {
                user.setBanned(true);
                userService.save(user);
            }
        });
        response.sendRedirect("/admin");
    }

    @PostMapping("/admin/users/{id}/unban")
    public void unbanUser(@PathVariable Long id, HttpServletResponse response) throws IOException {
        userService.findById(id).ifPresent(user -> {
            if (!user.isAdmin()) {
                user.setBanned(false);
                userService.save(user);
            }
        });
        response.sendRedirect("/admin");
    }

    @PostMapping("/admin/users/{id}/edit")
    public void editUser(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String additional,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String phone,
            HttpServletResponse response) throws IOException {

        userService.findById(id).ifPresent(user -> {
            // No permitir editar administradores
            if (!user.isAdmin()) {
                user.setName(name.trim());
                user.setEmail(email.trim());
                user.setFullName(fullName.trim());

                if (birthDate != null && !birthDate.isBlank()) {
                    try {
                        user.setBirthDate(LocalDate.parse(birthDate));
                    } catch (Exception e) {
                        // Ignorar si el formato es inválido
                    }
                }

                // Combinar campos de dirección en un solo string
                StringBuilder addressBuilder = new StringBuilder();
                if (street != null && !street.isBlank()) {
                    addressBuilder.append("Calle: ").append(street.trim()).append("\n");
                }
                if (additional != null && !additional.isBlank()) {
                    addressBuilder.append("Información adicional: ").append(additional.trim()).append("\n");
                }
                if (city != null && !city.isBlank()) {
                    addressBuilder.append("Ciudad: ").append(city.trim()).append("\n");
                }
                if (province != null && !province.isBlank()) {
                    addressBuilder.append("Provincia: ").append(province.trim()).append("\n");
                }
                if (postalCode != null && !postalCode.isBlank()) {
                    addressBuilder.append("Código Postal: ").append(postalCode.trim()).append("\n");
                }
                if (country != null && !country.isBlank()) {
                    addressBuilder.append("País: ").append(country.trim()).append("\n");
                }
                if (phone != null && !phone.isBlank()) {
                    addressBuilder.append("Teléfono: ").append(phone.trim());
                }

                user.setShippingAddress(addressBuilder.toString());
                userService.save(user);
            }
        });
        response.sendRedirect("/admin");
    }
}