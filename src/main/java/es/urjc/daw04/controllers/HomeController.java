package es.urjc.daw04.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
        public String home(@RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            Model model,
            HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", !principal.getName().isEmpty());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        }
        else{
            model.addAttribute("logged", false);
            model.addAttribute("admin", false);
        }

        var categories = categoryService.findAll();
        Long selectedCategoryId = categoryId;

        if (selectedCategoryId == null && !categories.isEmpty()) {
            selectedCategoryId = categories.get(0).getId();
        }

        String searchQuery = q == null ? "" : q.trim();

        if (selectedCategoryId == null) {
            model.addAttribute("products", List.of());
        } else if (!searchQuery.isEmpty()) {
            model.addAttribute("products", productService.searchByCategoryId(selectedCategoryId, searchQuery));
        } else {
            model.addAttribute("products", productService.findByCategoryId(selectedCategoryId));
        }

        List<Map<String, Object>> categoryViews = new ArrayList<>();
        String selectedCategoryName = "Plantas";

        for (var category : categories) {
            boolean selected = selectedCategoryId != null && selectedCategoryId.equals(category.getId());
            if (selected) {
                selectedCategoryName = category.getName();
            }
            Map<String, Object> view = new HashMap<>();
            view.put("id", category.getId());
            view.put("name", category.getName());
            view.put("icon", category.getIcon());
            view.put("selected", selected);
            categoryViews.add(view);
        }

        model.addAttribute("categories", categoryViews);
        model.addAttribute("selectedCategoryName", selectedCategoryName);
        model.addAttribute("selectedCategoryId", selectedCategoryId);
        model.addAttribute("searchQuery", searchQuery);

        return "home";
    }
}