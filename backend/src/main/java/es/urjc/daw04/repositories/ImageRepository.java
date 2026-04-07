package es.urjc.daw04.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.daw04.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
