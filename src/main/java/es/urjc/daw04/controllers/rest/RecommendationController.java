package es.urjc.daw04.controllers.rest;

import java.security.Principal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import es.urjc.daw04.service.RecommendationService;
import es.urjc.daw04.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.RecommendationPackDTO;
import es.urjc.daw04.model.dto.RecommendationResponseDTO;
import es.urjc.daw04.model.RecommendationPack;
import es.urjc.daw04.model.mapper.RecommendationMapper;

@RestController("restRecommendationController")
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private static final int RECOMMENDATION_LIMIT = 6;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendationMapper recommendationMapper;

    @GetMapping()
    public ResponseEntity<RecommendationResponseDTO> getRecommendations(HttpServletRequest request) {

        String title = "Productos más comprados";
        String subtitle = "Los productos más populares entre nuestros clientes";
        List<RecommendationPack> recommendations;

        Principal principal = request.getUserPrincipal();
        User user = null;

        if (principal != null) {
            user = userService.findByName(principal.getName()).orElse(null);
        }

        if (user != null) {
            title = "Recomendado para ti";
            subtitle = "Basado en tus compras anteriores y tus preferencias";
            recommendations = recommendationService.getRecommendations(user, RECOMMENDATION_LIMIT);

        } else {
            recommendations = recommendationService.getBestsellers(RECOMMENDATION_LIMIT);

        }

        List<RecommendationPackDTO> items = recommendationMapper.toDTOs(recommendations);

        return ResponseEntity.ok(new RecommendationResponseDTO(title, subtitle, items));
    }
}
