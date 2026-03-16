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

import org.springframework.web.bind.annotation.PutMapping;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


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
    public ProductDTO getProductById(@PathVariable long id) {
        Product product = productService.findById(id);
        return productMapper.toDTO(product);
    }
    
    @PostMapping("/")
    public ResponseEntity<ProductDTO> postProduct(@RequestBody Product product) {
        product = productService.save(product);
        ProductDTO productDTO = productMapper.toDTO(product);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(productDTO.id()).toUri();
        
        return ResponseEntity.created(location).body(productDTO);
    }
    
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable long id, @RequestBody Product product) {
        Product updatedProduct = productService.update(id, product);
        return productMapper.toDTO(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ProductDTO deleteProduct(@PathVariable long id) {
        Product product = productService.findById(id);
        productService.deleteById(id);
        return productMapper.toDTO(product);
    }
}
