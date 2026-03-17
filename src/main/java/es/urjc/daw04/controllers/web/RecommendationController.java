package es.urjc.daw04.controllers.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.RecommendationService;
import es.urjc.daw04.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RecommendationController {

    private static final int RECOMMENDATION_LIMIT = 6;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @GetMapping("/recommendations")
    public String recommendations(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        // Sidebar categories (none selected)
        List<Map<String, Object>> categoryViews = new ArrayList<>();
        for (var category : categoryService.findAll()) {
            Map<String, Object> view = new HashMap<>();
            view.put("id", category.getId());
            view.put("name", category.getName());
            view.put("icon", category.getIcon());
            view.put("selected", false);
            categoryViews.add(view);
        }
        model.addAttribute("categories", categoryViews);

        // Recommendations + title
        if (principal != null) {
            try {
                Long userId = Long.parseLong(principal.getName());
                var userOpt = userService.findById(userId);
                if (userOpt.isPresent()) {
                    model.addAttribute("recommendations",
                            recommendationService.getRecommendations(userOpt.get(), RECOMMENDATION_LIMIT));
                    model.addAttribute("recommendationTitle", "Recomendado para ti");
                    model.addAttribute("recommendationSubtitle",
                            "Basado en tus compras anteriores y tus preferencias");
                } else {
                    addBestsellers(model);
                }
            } catch (NumberFormatException e) {
                addBestsellers(model);
            }
        } else {
            addBestsellers(model);
        }

        return "recommendations";
    }

    private void addBestsellers(Model model) {
        model.addAttribute("recommendations",
                recommendationService.getBestsellers(RECOMMENDATION_LIMIT));
        model.addAttribute("recommendationTitle", "Lo más vendido");
        model.addAttribute("recommendationSubtitle",
                "Los productos más populares de nuestra tienda");
    }
}
