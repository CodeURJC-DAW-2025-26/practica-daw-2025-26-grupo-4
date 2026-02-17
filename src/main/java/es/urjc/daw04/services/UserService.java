
package es.urjc.daw04.services;

import org.springframework.stereotype.Service;

import es.urjc.daw04.model.User;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    public UserService() {
        // Usuario normal
        users.add(new User("u", "u", "USER"));
        // Administrador con datos completos
        users.add(new User(
            "a",
            "a",
            "ADMIN",
            LocalDate.of(2005, 9, 21),
            "Arturo Vinuesa",
            "calle nardos 7 1ªB Mostoles Madrid 28933"
        ));
    }

    public User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public boolean validateUser(String username, String password) {
        User user = findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public List<User> getAllUsers() {
        return users;
    }
}
