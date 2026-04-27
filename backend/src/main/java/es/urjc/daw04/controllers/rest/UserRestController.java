package es.urjc.daw04.controllers.rest;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ErrorResponseDTO;
import es.urjc.daw04.model.dto.SuccessResponseDTO;
import es.urjc.daw04.model.dto.UserAccountUpdateRequestDTO;
import es.urjc.daw04.model.dto.UserAddressUpdateRequestDTO;
import es.urjc.daw04.model.dto.UserPasswordChangeRequestDTO;
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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserMe(Principal principal) {
        return getCurrentUser(principal);
    }

    @PutMapping("/account")
    @Transactional
    public ResponseEntity<?> updateAccount(@RequestBody UserAccountUpdateRequestDTO request, Principal principal) {
        try {
            User user = userAccountService.updateAccount(
                    principal,
                    request.username(),
                    request.fullName(),
                    request.birthDate(),
                    true);
            return ResponseEntity.ok(toProfileResponse(user));
        } catch (IllegalArgumentException ex) {
            return toErrorResponse(ex.getMessage());
        }
    }

    @PutMapping("/address")
    @Transactional
    public ResponseEntity<?> saveAddress(@RequestBody UserAddressUpdateRequestDTO request, Principal principal) {
        try {
            User user = userAccountService.saveAddress(
                    principal,
                    request.street(),
                    request.additional(),
                    request.city(),
                    request.province(),
                    request.postalCode(),
                    request.country(),
                    request.phone(),
                    true);
            return ResponseEntity.ok(toProfileResponse(user));
        } catch (IllegalArgumentException ex) {
            return toErrorResponse(ex.getMessage());
        }
    }

    @DeleteMapping("/address")
    @Transactional
    public ResponseEntity<?> deleteAddress(Principal principal) {
        try {
            userAccountService.deleteAddress(principal);
            return ResponseEntity.ok(new SuccessResponseDTO("Dirección eliminada correctamente"));
        } catch (IllegalArgumentException ex) {
            return toErrorResponse(ex.getMessage());
        }
    }

    @PutMapping("/password")
    @Transactional
    public ResponseEntity<?> changePassword(@RequestBody UserPasswordChangeRequestDTO request, Principal principal) {
        try {
            userAccountService.changePassword(
                    principal,
                    request.oldPassword(),
                    request.newPassword(),
                    request.confirmPassword());
            return ResponseEntity.ok(new SuccessResponseDTO("Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException ex) {
            return toErrorResponse(ex.getMessage());
        }
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadProfileImage(@RequestParam MultipartFile profileImage, Principal principal) {
        try {
            User user = userAccountService.updateProfileImage(principal, profileImage);
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
