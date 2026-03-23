package es.urjc.daw04.controllers.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Image;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.dto.ImageDTO;
import es.urjc.daw04.model.dto.ProductCreateRequestDTO;
import es.urjc.daw04.model.dto.ProductDTO;
import es.urjc.daw04.model.dto.ProductUpdateRequestDTO;
import es.urjc.daw04.model.mapper.ImageMapper;
import es.urjc.daw04.model.mapper.ProductMapper;
import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ImageService;
import es.urjc.daw04.service.ProductService;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productService.findAll(pageable).map(productMapper::toDTO);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable long id) {
        Product product = productService.findById(id);
        return productMapper.toDTO(product);
    }
    
    @PostMapping("/")
    public ResponseEntity<ProductDTO> postProduct(@RequestBody ProductCreateRequestDTO request) {
        Product product = new Product(
            request.name(),
            request.price() != null ? request.price() : 0.0,
            request.description() != null ? request.description() : "",
            request.tags() != null ? request.tags() : java.util.List.of()
        );
        
        if (request.categoryId() != null) {
            categoryService.findById(request.categoryId()).ifPresent(product::setCategory);
        }

        product = productService.save(product);
        ProductDTO productDTO = productMapper.toDTO(product);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(productDTO.id()).toUri();
        
        return ResponseEntity.created(location).body(productDTO);
    }
    
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable long id, @RequestBody ProductUpdateRequestDTO request) {
        Product existingProduct = productService.findById(id);
        
        existingProduct.setName(request.name());
        existingProduct.setPrice(request.price() != null ? request.price() : 0.0);
        existingProduct.setDescription(request.description() != null ? request.description() : "");
        existingProduct.setTags(request.tags() != null ? request.tags() : java.util.List.of());

        if (request.categoryId() == null) {
            existingProduct.setCategory(null);
        } else {
            categoryService.findById(request.categoryId()).ifPresent(existingProduct::setCategory);
        }

        Product updatedProduct = productService.update(id, existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ProductDTO deleteProduct(@PathVariable long id) {
        Product product = productService.findById(id);
        productService.deleteById(id);
        return productMapper.toDTO(product);
    }

    @PostMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile);
        productService.addImageToProduct(id, image);

        // Get the latest persisted image with an ID from the product
        Product product = productService.findById(id);
        Image persistedImage = product.getImages().get(product.getImages().size() - 1);

        URI location = fromCurrentContextPath()
            .path("/api/v1/images/{imageId}/media")
                .buildAndExpand(persistedImage.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(persistedImage));
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ImageDTO deleteProductImage(@PathVariable long productId, @PathVariable long imageId)
            throws IOException {

        Image image = imageService.getImage(imageId);
        productService.removeImageFromProduct(productId, imageId);
        imageService.deleteImage(imageId);

        return imageMapper.toDTO(image);
    }
}
