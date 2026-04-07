package es.urjc.daw04.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.ErrorResponseDTO;
import es.urjc.daw04.model.dto.RegisterRequestDTO;
import es.urjc.daw04.model.dto.RegisterResponseDTO;
import es.urjc.daw04.security.jwt.AuthResponse;
import es.urjc.daw04.security.jwt.AuthResponse.Status;
import es.urjc.daw04.security.jwt.LoginRequest;
import es.urjc.daw04.security.jwt.UserLoginService;																																																																																																																																																													
import es.urjc.daw04.service.AuthRegistrationService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginRestController {
	
	@Autowired
	private UserLoginService userService;

	@Autowired
	private AuthRegistrationService authRegistrationService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		
		return userService.login(response, loginRequest);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userService.refresh(response, refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
		try {
			User user = authRegistrationService.registerUser(
					request.fullName(),
					request.username(),
					request.email(),
					request.password(),
					request.confirmPassword());

			RegisterResponseDTO response = new RegisterResponseDTO(
					user.getId(),
					user.getName(),
					user.getFullName(),
					user.getEmail(),
					user.getRoles());

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(new ErrorResponseDTO(ex.getMessage()));
		}
	}
}