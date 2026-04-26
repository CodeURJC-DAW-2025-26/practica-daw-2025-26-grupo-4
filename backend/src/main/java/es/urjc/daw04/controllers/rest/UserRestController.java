package es.urjc.daw04.controllers.rest;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ErrorResponseDTO;
import es.urjc.daw04.model.dto.UserProfileResponseDTO;
import es.urjc.daw04.service.UserAccountService;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        User user = userAccountService.findCurrentUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO("Usuario no autenticado"));
        }

        return ResponseEntity.ok(toProfileResponse(user));
    }

    private UserProfileResponseDTO toProfileResponse(User user) {
        String profileImageUrl = user.getProfileImage() != null
                ? "/api/v1/images/" + user.getProfileImage().getId() + "/media"
                : null;
        return new UserProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getFullName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getShippingAddress(),
                user.getRoles(),
                profileImageUrl);
    }
}
