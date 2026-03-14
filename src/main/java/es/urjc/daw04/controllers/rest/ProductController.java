package es.urjc.daw04.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.dto.ProductDTO;
import es.urjc.daw04.model.mapper.ProductMapper;
import es.urjc.daw04.service.ProductService;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    //! TODO: 
    //! 2. Params
    //! 3. AJAX
    //! 4. Images
    @GetMapping("/")
    public Collection<ProductDTO> getProducts() {
        return productMapper.toDTOs(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable long id) {
        Product product = productService.findById(id);
        if (product != null) {
            return ResponseEntity.ok(productMapper.toDTO(product));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/")
    public ResponseEntity<ProductDTO> postProduct(@RequestBody Product product) {
        product = productService.save(product);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(location).body(productMapper.toDTO(product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable long id, @RequestBody Product product) {
        Product updatedProduct = productService.update(id, product);
        if (updatedProduct != null) {
            return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        Product product = productService.findById(id);
        if (product != null) {
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
