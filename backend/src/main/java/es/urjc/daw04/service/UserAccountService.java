package es.urjc.daw04.service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw04.model.Image;
import es.urjc.daw04.model.User;
import es.urjc.daw04.model.dto.UserUpdateRequestDTO;
import es.urjc.daw04.repositories.UserRepository;

@Service
public class UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    private static final long MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp");

    public User findCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return null;
        }

        String principalName = principal.getName();
        User user = userRepository.findByName(principalName).orElse(null);
        if (user != null) {
            return user;
        }

        try {
            return userRepository.findById(Long.valueOf(principalName)).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Transactional
    public User updateAccount(Principal principal, String username, String fullName, String birthDate,
            boolean failOnInvalidBirthDate) {
        User user = requireCurrentUser(principal);

        if (username != null && !username.isBlank() && !username.equals(user.getName())) {
            if (userRepository.findByName(username).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
            user.setName(username);
        }

        if (fullName != null && !fullName.isBlank() && !fullName.startsWith("Establecer")) {
            user.setFullName(fullName);
        }

        if (birthDate != null && !birthDate.isBlank()) {
            try {
                user.setBirthDate(LocalDate.parse(birthDate));
            } catch (Exception ex) {
                if (failOnInvalidBirthDate) {
                    throw new IllegalArgumentException("Fecha de nacimiento inválida");
                }
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Principal principal, UserUpdateRequestDTO request) throws IOException {
        User user = requireCurrentUser(principal);

        if (request.getUsername() != null && !request.getUsername().isBlank()
                && !request.getUsername().equals(user.getName())) {
            if (userRepository.findByName(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
            user.setName(request.getUsername());
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()
                && !request.getFullName().startsWith("Establecer")) {
            user.setFullName(request.getFullName());
        }

        if (request.getBirthDate() != null && !request.getBirthDate().isBlank()) {
            try {
                user.setBirthDate(LocalDate.parse(request.getBirthDate()));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Fecha de nacimiento inválida");
            }
        }

        boolean passwordRequested = hasText(request.getOldPassword()) || hasText(request.getNewPassword())
                || hasText(request.getConfirmPassword());
        if (passwordRequested) {
            if (!hasText(request.getOldPassword()) || !hasText(request.getNewPassword())
                    || !hasText(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Todos los campos de contraseña son obligatorios");
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getEncodedPassword())) {
                throw new IllegalArgumentException("La contraseña antigua es incorrecta");
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Las nuevas contraseñas no coinciden");
            }

            if (request.getOldPassword().equals(request.getNewPassword())) {
                throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la antigua");
            }

            user.setEncodedPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.isClearAddress()) {
            user.setShippingAddress(null);
        } else if (hasAddressData(request)) {
            if (!hasText(request.getStreet())) {
                throw new IllegalArgumentException("La calle es obligatoria");
            }

            user.setShippingAddress(buildShippingAddress(
                    request.getStreet(),
                    request.getAdditional(),
                    request.getCity(),
                    request.getProvince(),
                    request.getPostalCode(),
                    request.getCountry(),
                    request.getPhone()));
        }

        MultipartFile imageFile = request.getProfileImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Formato de imagen no permitido. Usa JPEG, PNG o WebP");
            }

            if (imageFile.getSize() > MAX_PROFILE_IMAGE_SIZE) {
                throw new IllegalArgumentException("La imagen no puede superar los 5 MB");
            }

            Image newImage = imageService.createImage(imageFile);
            user.setProfileImage(newImage);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User saveAddress(Principal principal, String street, String additional, String city, String province,
            String postalCode, String country, String phone, boolean requireStreet) {

        User user = requireCurrentUser(principal);

        if (street == null || street.isBlank()) {
            if (requireStreet) {
                throw new IllegalArgumentException("La calle es obligatoria");
            }
            return user;
        }

        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(street);

        if (additional != null && !additional.isBlank()) {
            addressBuilder.append("\n").append(additional);
        }
        if (city != null && province != null && postalCode != null
                && !city.isBlank() && !province.isBlank() && !postalCode.isBlank()) {
            addressBuilder.append("\n")
                    .append(city)
                    .append(", ")
                    .append(province)
                    .append(" ")
                    .append(postalCode);
        }
        if (country != null && !country.isBlank()) {
            addressBuilder.append("\n").append(country);
        }
        if (phone != null && !phone.isBlank()) {
            addressBuilder.append("\nTeléfono: ").append(phone);
        }

        user.setShippingAddress(addressBuilder.toString());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteAddress(Principal principal) {
        User user = requireCurrentUser(principal);
        user.setShippingAddress(null);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Principal principal, String oldPassword, String newPassword, String confirmPassword) {        User user = requireCurrentUser(principal);

        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            throw new IllegalArgumentException("Todos los campos de contraseña son obligatorios");
        }

        if (!passwordEncoder.matches(oldPassword, user.getEncodedPassword())) {
            throw new IllegalArgumentException("La contraseña antigua es incorrecta");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las nuevas contraseñas no coinciden");
        }

        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la antigua");
        }

        user.setEncodedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User requireCurrentUser(Principal principal) {
        User user = findCurrentUser(principal);
        if (user == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }
        return user;
    }

    @Transactional
    public User updateProfileImage(Principal principal, MultipartFile imageFile) throws IOException {
        User user = requireCurrentUser(principal);

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("La imagen no puede estar vacía");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Formato de imagen no permitido. Usa JPEG, PNG o WebP");
        }

        if (imageFile.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new IllegalArgumentException("La imagen no puede superar los 5 MB");
        }

        Image newImage = imageService.createImage(imageFile);
        user.setProfileImage(newImage);
        return userRepository.save(user);
    }

    private boolean hasAddressData(UserUpdateRequestDTO request) {
        return hasText(request.getStreet()) || hasText(request.getAdditional()) || hasText(request.getCity())
                || hasText(request.getProvince()) || hasText(request.getPostalCode()) || hasText(request.getCountry())
                || hasText(request.getPhone());
    }

    private String buildShippingAddress(String street, String additional, String city, String province,
            String postalCode, String country, String phone) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(street);

        if (hasText(additional)) {
            addressBuilder.append("\n").append(additional);
        }
        if (hasText(city) && hasText(province) && hasText(postalCode)) {
            addressBuilder.append("\n")
                    .append(city)
                    .append(", ")
                    .append(province)
                    .append(" ")
                    .append(postalCode);
        }
        if (hasText(country)) {
            addressBuilder.append("\n").append(country);
        }
        if (hasText(phone)) {
            addressBuilder.append("\nTeléfono: ").append(phone);
        }

        return addressBuilder.toString();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
