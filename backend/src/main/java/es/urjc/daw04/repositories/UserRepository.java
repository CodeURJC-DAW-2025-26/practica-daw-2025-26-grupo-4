package es.urjc.daw04.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.urjc.daw04.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);

}
