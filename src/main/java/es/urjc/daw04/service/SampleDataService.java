package es.urjc.daw04.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import es.urjc.daw04.model.Review;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SampleDataService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Crear usuarios de prueba
        if (userRepository.findByName("user").isEmpty()) {
            User userNormal = new User();
            userNormal.setName("user");
            userNormal.setEmail("pruebas@gmail.com");
            userNormal.setEncodedPassword(passwordEncoder.encode("user"));
            userNormal.setRoles(List.of("USER"));
            userRepository.save(userNormal);
        }

        if (userRepository.findByName("admin").isEmpty()) {
            User userAdmin = new User();
            userAdmin.setName("admin");
            userAdmin.setEncodedPassword(passwordEncoder.encode("admin"));
            userAdmin.setRoles(List.of("ADMIN"));
            userRepository.save(userAdmin);
        }

        // Crear categorías
        Category plantas = new Category("Plantas", "plantas", "fa-solid fa-leaf");
        Category suelo = new Category("Suelo", "suelo", "fa-solid fa-seedling");
        Category herramientas = new Category("Herramientas", "herramientas", "fa-solid fa-hammer");
        Category cuidado = new Category("Cuidado", "cuidado", "fa-solid fa-droplet");

        if (categoryService.findAll().isEmpty()) {
            categoryService.save(plantas);
            categoryService.save(suelo);
            categoryService.save(herramientas);
            categoryService.save(cuidado);
        }

        List<Category> categories = categoryService.findAll();
        Category catPlantas = categories.stream().filter(c -> c.getName().equals("Plantas")).findFirst().orElse(null);
        Category catSuelo = categories.stream().filter(c -> c.getName().equals("Suelo")).findFirst().orElse(null);
        Category catHerramientas = categories.stream().filter(c -> c.getName().equals("Herramientas")).findFirst().orElse(null);
        Category catCuidado = categories.stream().filter(c -> c.getName().equals("Cuidado")).findFirst().orElse(null);

        // Solo cargamos datos si la base de datos está vacía
        if (productService.findAll().isEmpty()) {
            // ===== PLANTAS =====
            Product p1 = new Product("Monstera Deliciosa", 45.99, "Planta de interior grande", List.of("Verde"), 
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p1.setCategory(catPlantas);
            productService.save(p1);

            Product p2 = new Product("Pothos Dorado", 22.50, "Planta trepadora de fácil cuidado", List.of("Verde", "Amarillo"),
                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop"));
            p2.setCategory(catPlantas);
            productService.save(p2);

            Product p3 = new Product("Suculenta Jade", 18.99, "Pequeña planta carnuda", List.of("Verde"),
                List.of("https://images.unsplash.com/photo-1459156212016-c812468e2115?q=80&w=600&auto=format&fit=crop"));
            p3.setCategory(catPlantas);
            productService.save(p3);

            Product p4 = new Product("Cactus Parodia", 15.50, "Cactus decorativo pequeño", List.of("Verde"),
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p4.setCategory(catPlantas);
            productService.save(p4);

            Product p5 = new Product("Planta Araña Variegada", 24.99, "Planta colgante de larga vida", List.of("Blanco", "Verde"),
                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop"));
            p5.setCategory(catPlantas);
            productService.save(p5);

            Product p6 = new Product("Ficus Lyrata", 55.00, "Higuera de hoja de violín", List.of("Verde oscuro"),
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p6.setCategory(catPlantas);
            productService.save(p6);

            Product p7 = new Product("Palma Areca", 38.75, "Palma tropical de interior", List.of("Verde"),
                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop"));
            p7.setCategory(catPlantas);
            productService.save(p7);

            Product p8 = new Product("Planta Serpiente", 28.00, "Sansevieria trifasciata", List.of("Verde", "Amarillo"),
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p8.setCategory(catPlantas);
            productService.save(p8);

            Product p9 = new Product("Filodendro Rosa", 42.50, "Filodendro rosa de terciopelo", List.of("Rosa", "Púrpura"),
                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop"));
            p9.setCategory(catPlantas);
            productService.save(p9);

            Product p10 = new Product("Calatea Orbifolia", 35.99, "Planta decorativa elegante", List.of("Verde", "Blanco"),
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p10.setCategory(catPlantas);
            productService.save(p10);

            Product p11 = new Product("Begonia Maculada", 32.00, "Begonia de flores blancas", List.of("Rojo", "Blanco"),
                List.of("https://images.unsplash.com/photo-1545239705-1564e58b9e4a?q=80&w=600&auto=format&fit=crop"));
            p11.setCategory(catPlantas);
            productService.save(p11);

            Product p12 = new Product("Peperomia Roja", 19.99, "Planta pequeña y decorativa", List.of("Rojo", "Verde"),
                List.of("https://images.unsplash.com/photo-1614594975525-e45190c55d0b?q=80&w=600&auto=format&fit=crop"));
            p12.setCategory(catPlantas);
            productService.save(p12);

            // ===== SUELO =====
            Product s1 = new Product("Tierra Universal Premium", 12.99, "Sustrato universal de calidad", List.of("Marrón"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s1.setCategory(catSuelo);
            productService.save(s1);

            Product s2 = new Product("Sustrato para Cactus", 14.50, "Mezcla drenante especial", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s2.setCategory(catSuelo);
            productService.save(s2);

            Product s3 = new Product("Turba Rubia Natural", 11.75, "Turba sin abonos añadidos", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s3.setCategory(catSuelo);
            productService.save(s3);

            Product s4 = new Product("Perlita de Sílice", 9.99, "Agente drenante ligero", List.of("Blanco"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s4.setCategory(catSuelo);
            productService.save(s4);

            Product s5 = new Product("Carbón Activado", 13.25, "Previene hongos y bacterias", List.of("Negro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s5.setCategory(catSuelo);
            productService.save(s5);

            Product s6 = new Product("Musgo Sphagnum Seco", 16.99, "Musgo de turba natural", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s6.setCategory(catSuelo);
            productService.save(s6);

            Product s7 = new Product("Arena de Sílice Gruesa", 10.50, "Arena para mejora de drenaje", List.of("Blanco"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s7.setCategory(catSuelo);
            productService.save(s7);

            Product s8 = new Product("Vermiculita Agrícola", 12.75, "Mantiene la humedad equilibrada", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s8.setCategory(catSuelo);
            productService.save(s8);

            Product s9 = new Product("Zeolita Volcánica", 15.49, "Mejora la aireación del suelo", List.of("Gris claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s9.setCategory(catSuelo);
            productService.save(s9);

            Product s10 = new Product("Corteza de Pino Molida", 11.25, "Sustrato para orquídeas", List.of("Marrón"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s10.setCategory(catSuelo);
            productService.save(s10);

            Product s11 = new Product("Fibra de Coco Prensada", 13.99, "Sustrato ecológico versátil", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s11.setCategory(catSuelo);
            productService.save(s11);

            Product s12 = new Product("Mezcla Drenante Premium", 17.50, "Mix perfecto para suculentas", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1574943320219-553eb213f72d?q=80&w=600&auto=format&fit=crop"));
            s12.setCategory(catSuelo);
            productService.save(s12);

            // ===== HERRAMIENTAS =====
            Product h1 = new Product("Pala de Trasplante Pequeña", 8.99, "Pala metálica para macetas", List.of("Plateado"),
                List.of("https://images.unsplash.com/photo-1586432078519-a81e77320882?q=80&w=600&auto=format&fit=crop"));
            h1.setCategory(catHerramientas);
            productService.save(h1);

            Product h2 = new Product("Rastrillo de Mano", 9.75, "Rastrillo pequeño para suelo", List.of("Plateado", "Negro"),
                List.of("https://images.unsplash.com/photo-1585771724684-38269d6639fd?q=80&w=600&auto=format&fit=crop"));
            h2.setCategory(catHerramientas);
            productService.save(h2);

            Product h3 = new Product("Podadera Manual", 16.50, "Tijeras de poda profesionales", List.of("Plateado"),
                List.of("https://images.unsplash.com/photo-1588195538326-c5b1e6f2e0d5?q=80&w=600&auto=format&fit=crop"));
            h3.setCategory(catHerramientas);
            productService.save(h3);

            Product h4 = new Product("Regadera de 2 Litros", 14.25, "Regadera con rosa intercambiable", List.of("Verde", "Plateado"),
                List.of("https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=600&auto=format&fit=crop"));
            h4.setCategory(catHerramientas);
            productService.save(h4);

            Product h5 = new Product("Maceta Cerámica 30cm", 24.99, "Maceta decorativa con drenaje", List.of("Blanco", "Gris"),
                List.of("https://images.unsplash.com/photo-1577720643272-265f434884b3?q=80&w=600&auto=format&fit=crop"));
            h5.setCategory(catHerramientas);
            productService.save(h5);

            Product h6 = new Product("Maceta Plástica 20cm", 5.99, "Maceta económica con agujeros", List.of("Negro"),
                List.of("https://images.unsplash.com/photo-1563241527-3004d3cb4e3b?q=80&w=600&auto=format&fit=crop"));
            h6.setCategory(catHerramientas);
            productService.save(h6);

            Product h7 = new Product("Soporte para Plantas", 19.50, "Estructura metálica para macetas", List.of("Negro"),
                List.of("https://images.unsplash.com/photo-1596859181486-b0aca3d0aac6?q=80&w=600&auto=format&fit=crop"));
            h7.setCategory(catHerramientas);
            productService.save(h7);

            Product h8 = new Product("Alambre de Jardín Enrollado", 7.75, "Alambre flexible de 50m", List.of("Verde"),
                List.of("https://images.unsplash.com/photo-1585771724684-38269d6639fd?q=80&w=600&auto=format&fit=crop"));
            h8.setCategory(catHerramientas);
            productService.save(h8);

            Product h9 = new Product("Guantes de Jardín Premium", 12.50, "Guantes protectores de nitrilo", List.of("Negro"),
                List.of("https://images.unsplash.com/photo-1584265618000-d2c02f8b4f3d?q=80&w=600&auto=format&fit=crop"));
            h9.setCategory(catHerramientas);
            productService.save(h9);

            Product h10 = new Product("Pulverizador de 1.5L", 11.99, "Spray para riego y pesticidas", List.of("Transparente"),
                List.of("https://images.unsplash.com/photo-1617791160537-b13ef63c6f60?q=80&w=600&auto=format&fit=crop"));
            h10.setCategory(catHerramientas);
            productService.save(h10);

            Product h11 = new Product("Tijeras de Poda Profesionales", 18.75, "Corte limpio y preciso", List.of("Rojo"),
                List.of("https://images.unsplash.com/photo-1585771724684-38269d6639fd?q=80&w=600&auto=format&fit=crop"));
            h11.setCategory(catHerramientas);
            productService.save(h11);

            Product h12 = new Product("Cuerdas para Plantas", 6.50, "Cuerda de algodón para sostén", List.of("Marrón"),
                List.of("https://images.unsplash.com/photo-1591150397211-2b0ed5c67b2f?q=80&w=600&auto=format&fit=crop"));
            h12.setCategory(catHerramientas);
            productService.save(h12);

            // ===== CUIDADO =====
            Product c1 = new Product("Fertilizante Universal Líquido", 13.99, "NPK 8-8-8 para todas las plantas", List.of("Marrón"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c1.setCategory(catCuidado);
            productService.save(c1);

            Product c2 = new Product("Fungicida Ecológico", 15.50, "Previene y trata hongos naturalmente", List.of("Amarillo"),
                List.of("https://images.unsplash.com/photo-1619451334792-150e80b96e0f?q=80&w=600&auto=format&fit=crop"));
            c2.setCategory(catCuidado);
            productService.save(c2);

            Product c3 = new Product("Insecticida Natural de Neem", 16.99, "Control biológico de plagas", List.of("Amarillo claro"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c3.setCategory(catCuidado);
            productService.save(c3);

            Product c4 = new Product("Vitaminas para Plantas", 17.25, "Estimulante de crecimiento", List.of("Azul"),
                List.of("https://images.unsplash.com/photo-1617791160537-b13ef63c6f60?q=80&w=600&auto=format&fit=crop"));
            c4.setCategory(catCuidado);
            productService.save(c4);

            Product c5 = new Product("Enraizante Gel", 14.75, "Acelera el enraizamiento de esquejes", List.of("Transparente"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c5.setCategory(catCuidado);
            productService.save(c5);

            Product c6 = new Product("Repelente Anti-Plagas", 12.50, "Protección contra insectos", List.of("Blanco"),
                List.of("https://images.unsplash.com/photo-1619451334792-150e80b96e0f?q=80&w=600&auto=format&fit=crop"));
            c6.setCategory(catCuidado);
            productService.save(c6);

            Product c7 = new Product("Regulador pH Down", 9.99, "Reduce el pH del sustrato", List.of("Transparente"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c7.setCategory(catCuidado);
            productService.save(c7);

            Product c8 = new Product("Extracto de Alga Marina", 18.50, "Estimulante natural de crecimiento", List.of("Marrón"),
                List.of("https://images.unsplash.com/photo-1617791160537-b13ef63c6f60?q=80&w=600&auto=format&fit=crop"));
            c8.setCategory(catCuidado);
            productService.save(c8);

            Product c9 = new Product("Ácidos Húmicos Granulados", 11.99, "Mejora la estructura del suelo", List.of("Negro"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c9.setCategory(catCuidado);
            productService.save(c9);

            Product c10 = new Product("Micorrizas en Polvo", 19.75, "Potencia la absorción de nutrientes", List.of("Marrón claro"),
                List.of("https://images.unsplash.com/photo-1619451334792-150e80b96e0f?q=80&w=600&auto=format&fit=crop"));
            c10.setCategory(catCuidado);
            productService.save(c10);

            Product c11 = new Product("Bacterias Benéficas Líquidas", 20.99, "Mejora la microbiología del sustrato", List.of("Blanco lechoso"),
                List.of("https://images.unsplash.com/photo-1598282768172-21191dfc0b7f?q=80&w=600&auto=format&fit=crop"));
            c11.setCategory(catCuidado);
            productService.save(c11);

            Product c12 = new Product("Aceite de Neem Concentrado", 21.50, "Tratamiento integral contra plagas", List.of("Amarillo"),
                List.of("https://images.unsplash.com/photo-1617791160537-b13ef63c6f60?q=80&w=600&auto=format&fit=crop"));
            c12.setCategory(catCuidado);
            productService.save(c12);

            // Agregar reviews a algunos productos
            reviewService.save(new Review(p1, "Agustin51", "Monstera hermosa y robusta. Excelente calidad.", 5));
            reviewService.save(new Review(p1, "Eduardo", "Llego bien empaquetada, muy contento.", 5));
            reviewService.save(new Review(p2, "Ampeterby7", "Crece muy rápido, perfecta para trepar.", 5));
            reviewService.save(new Review(p2, "XxConchaxX", "Tolera la sombra, ideal para interiores.", 4.5));
            reviewService.save(new Review(p3, "Maria", "Suculenta hermosa y de fácil cuidado.", 5));

            System.out.println("Base de datos inicializada con éxito. 48 productos cargados en 4 categorías.");
        }

        if (orderService.findAll().isEmpty()) {
            List<Product> products = productService.findAll();
            if (products.size() >= 2) {
                Product first = products.get(0);
                Product second = products.get(1);
                orderService.save(new Order(
                        new ArrayList<CartItem>(List.of(new CartItem(first, 2), new CartItem(second, 1)))));
            }
        }
    }
}