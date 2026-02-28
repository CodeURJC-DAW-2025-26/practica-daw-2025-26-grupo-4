package es.urjc.daw04.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private static final int HOME_PAGE_SIZE = 6;

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

        Page<?> firstPage;
        if (selectedCategoryId == null) {
            model.addAttribute("products", List.of());
            firstPage = Page.empty();
        } else if (!searchQuery.isEmpty()) {
            firstPage = productService.searchByCategoryIdPaged(selectedCategoryId, searchQuery, 0, HOME_PAGE_SIZE);
            model.addAttribute("products", firstPage.getContent());
        } else {
            firstPage = productService.findByCategoryIdPaged(selectedCategoryId, 0, HOME_PAGE_SIZE);
            model.addAttribute("products", firstPage.getContent());
        }
        model.addAttribute("hasMore", !firstPage.isEmpty() && firstPage.hasNext());

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

    @GetMapping("/api/products/fragment")
    public String productsFragment(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        String searchQuery = q == null ? "" : q.trim();

        if (categoryId == null) {
            model.addAttribute("products", List.of());
        } else if (!searchQuery.isEmpty()) {
            Page<?> p = productService.searchByCategoryIdPaged(categoryId, searchQuery, page, HOME_PAGE_SIZE);
            model.addAttribute("products", p.getContent());
        } else {
            Page<?> p = productService.findByCategoryIdPaged(categoryId, page, HOME_PAGE_SIZE);
            model.addAttribute("products", p.getContent());
        }

        return "fragments/home-products";
    }
}