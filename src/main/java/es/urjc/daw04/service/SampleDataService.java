package es.urjc.daw04.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.urjc.daw04.model.Product;
import java.util.List;

@Service
public class SampleDataService {

    @Autowired
    private ProductService productService;

    @PostConstruct
    public void init() {
        // Solo cargamos datos si la base de datos está vacía 
        if (productService.findAll().isEmpty()) {
            productService.save(new Product("Arizónica del viento", 35.55, "Planta de interior", 
                List.of("Verde"), List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop", "https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop")));
            productService.save(new Product("Lavanda oscura", 25.00, "Aromática", 
                List.of("Morado"), List.of("https://images.unsplash.com/photo-1459156212016-c812468e2115?q=80&w=600&auto=format&fit=crop")));
            
            System.out.println("Base de datos inicializada con éxito.");
        }
    }
}