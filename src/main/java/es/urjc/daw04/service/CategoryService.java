package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Category;
import es.urjc.daw04.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Optional<Category> findById(long id) {
        return repository.findById(id);
    }

    public void save(Category category) {
        repository.save(category);
    }
}
