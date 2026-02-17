package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Optional<Product> findById(long id) {
        return repository.findById(id);
    }

    public void save(Product product) {
        repository.save(product);
    }
}