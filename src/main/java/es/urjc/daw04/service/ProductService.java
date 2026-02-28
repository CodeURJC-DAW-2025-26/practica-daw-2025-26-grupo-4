package es.urjc.daw04.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.urjc.daw04.model.Product;
import es.urjc.daw04.repositories.CartItemRepository;
import es.urjc.daw04.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Page<Product> findAllPaged(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Optional<Product> findById(long id) {
        return repository.findById(id);
    }

    public List<Product> findByCategoryId(long categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    public Page<Product> findByCategoryIdPaged(long categoryId, int page, int size) {
        return repository.findByCategoryId(categoryId, PageRequest.of(page, size));
    }

    public List<Product> searchByCategoryId(long categoryId, String query) {
        return repository.searchByCategoryId(categoryId, query);
    }

    public Page<Product> searchByCategoryIdPaged(long categoryId, String query, int page, int size) {
        return repository.searchByCategoryIdPaged(categoryId, query, PageRequest.of(page, size));
    }

    public void save(Product product) {
        repository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteByProductId(id);
        repository.deleteById(id);
    }
}