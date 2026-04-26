package es.urjc.daw04.controllers.rest;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ErrorResponseDTO;
import es.urjc.daw04.model.dto.UserProfileResponseDTO;
import es.urjc.daw04.model.dto.UserUpdateRequestDTO;
import es.urjc.daw04.service.UserAccountService;

@RestController
@RequestMapping("/api/users")
public class UsersRestController {

    @Autowired
    private UserAccountService userAccountService;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateUser(@ModelAttribute UserUpdateRequestDTO request, Principal principal) {
        try {
            User user = userAccountService.updateUser(principal, request);
            return ResponseEntity.ok(toProfileResponse(user));
        } catch (IllegalArgumentException ex) {
            return toErrorResponse(ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(500).body(new ErrorResponseDTO("Error al subir la imagen"));
        }
    }

    private ResponseEntity<ErrorResponseDTO> toErrorResponse(String message) {
        if ("Usuario no autenticado".equals(message)) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO(message));
        }
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(message));
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
