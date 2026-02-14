package es.urjc.daw04.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import es.urjc.daw04.model.Product;

@Service
public class ProductService {

        List<Product> products = new ArrayList<>();

        public ProductService() {
                products.add(new Product(
                                1L,
                                "Arizónica del viento",
                                35.55,
                                "Una planta de interior que se adapta a cualquier ambiente",
                                List.of("Cálido", "Verde", "Decorativa"),
                                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop",
                                                "https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop")));

                products.add(new Product(
                                2L,
                                "Planta trepadora de universos",
                                55.00,
                                "Una planta trepadora que se adapta a cualquier ambiente",
                                List.of("Cálido", "Verde", "Decorativa"),
                                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop")));

                products.add(new Product(
                                3L,
                                "Lavanda oscura",
                                25.00,
                                "La lavanda oscura es una planta de interior que enriquece el olor de cualquier ambiente",
                                List.of("Morado", "Interior", "Aromática"),
                                List.of("https://images.unsplash.com/photo-1459156212016-c812468e2115?q=80&w=600&auto=format&fit=crop")));
        }

        public List<Product> findAll() {
                return products;
        }

        public Product findById(Long id) {
                for (Product product : products) {
                        if (product.getId() == id) {
                                return product;
                        }
                }
                return null;
        }
}