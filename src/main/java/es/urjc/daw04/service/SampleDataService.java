package es.urjc.daw04.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import es.urjc.daw04.model.Review;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Product;
import java.util.List;

@Service
public class SampleDataService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @PostConstruct
    public void init() {
        if (productService.findAll().isEmpty()) {
            Product p1 = new Product("Arizónica del viento", 35.55, "Planta de interior",
                    List.of("Verde"),
                    List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            productService.save(p1);

            Product p2 = new Product("Lavanda oscura", 25.00, "Aromática",
                    List.of("Morado"),
                    List.of("https://images.unsplash.com/photo-1459156212016-c812468e2115?q=80&w=600&auto=format&fit=crop"));
            productService.save(p2);

            reviewService.save(new Review(p1, "Agustin51", "Todo perfecto. 10 de 10.", 5));
            reviewService.save(new Review(p1, "Eduardo", "La planta ha llegado un poco seca...", 2.5));

            reviewService.save(new Review(p2, "Ampeterby7", "Ningún problema, queda perfecta para el baño.", 5));
            reviewService.save(
                    new Review(p2, "XxConchaxX", "Huele bastante bien, pero más fuerte de lo que esperábamos", 3.5));

            System.out.println("Base de datos inicializada con éxito.");
        }
    }
}