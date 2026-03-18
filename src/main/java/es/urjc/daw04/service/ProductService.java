package es.urjc.daw04.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Product> findAllPaged(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Product findById(long id) {
        return repository.findById(id).orElseThrow();
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

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product update(long id, Product product) {
        product.setId(id);
        return repository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteByProductId(id);
        repository.deleteById(id);
    }

    public void addImageToProduct(long id, es.urjc.daw04.model.Image image) {
        Product product = findById(id);
        product.getImages().add(image);
        save(product);
    }

    public void removeImageFromProduct(long productId, long imageId) {
        Product product = findById(productId);
        product.getImages().removeIf(image -> image.getId().equals(imageId));
        save(product);
    }
}