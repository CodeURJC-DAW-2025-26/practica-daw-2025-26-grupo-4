package es.urjc.daw04.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import es.urjc.daw04.model.Review;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.urjc.daw04.model.CartItem;
import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.EnumStatus;
import es.urjc.daw04.model.Order;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.User;
import es.urjc.daw04.repositories.UserRepository;

import java.util.Calendar;
import java.util.Date;
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

    @Autowired
    private ImageService imageService;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String IMG_BASE = "classpath:static/images/products/";

    private void addImage(Product product, String relativePath) {
        try {
            Resource resource = resourceLoader.getResource(IMG_BASE + relativePath);
            if (!resource.exists()) {
                System.err.println("Imagen no encontrada: " + relativePath);
                return;
            }
            product.getImages().add(imageService.createImageFromResource(resource));
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + relativePath + ": " + e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        User userNormal = userRepository.findByName("user").orElse(null);
        if (userRepository.findByName("user").isEmpty()) {
            userNormal = new User();
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

        // Create categories
        Category plants = new Category("Plantas", "plantas", "fa-solid fa-leaf");
        Category soil = new Category("Suelo", "suelo", "fa-solid fa-seedling");
        Category tools = new Category("Herramientas", "herramientas", "fa-solid fa-hammer");
        Category care = new Category("Cuidado", "cuidado", "fa-solid fa-droplet");

        if (categoryService.findAll().isEmpty()) {
            categoryService.save(plants);
            categoryService.save(soil);
            categoryService.save(tools);
            categoryService.save(care);
        }

        List<Category> categories = categoryService.findAll();
        Category catPlants = categories.stream().filter(c -> c.getName().equals("Plantas")).findFirst().orElse(null);
        Category catSoil = categories.stream().filter(c -> c.getName().equals("Suelo")).findFirst().orElse(null);
        Category catTools = categories.stream().filter(c -> c.getName().equals("Herramientas")).findFirst()
                .orElse(null);
        Category catCare = categories.stream().filter(c -> c.getName().equals("Cuidado")).findFirst().orElse(null);

        // Only load data if the database is empty
        if (productService.findAll().isEmpty()) {
            // ===== PLANTS =====
            Product p1 = new Product("Monstera Deliciosa", 45.99, "Planta de interior grande", List.of("Verde"));
            p1.setCategory(catPlants);
            addImage(p1, "plants/lnitsyk-houseplant-7367379_1920.jpg");
            productService.save(p1);

            Product p2 = new Product("Pothos Dorado", 22.50, "Planta trepadora de fácil cuidado",
                    List.of("Verde", "Amarillo"));
            p2.setCategory(catPlants);
            addImage(p2, "plants/ignartonosbg-green-leaves-7525661_1920.jpg");
            productService.save(p2);

            Product p3 = new Product("Suculenta Jade", 18.99, "Pequeña planta carnuda", List.of("Verde"));
            p3.setCategory(catPlants);
            addImage(p3, "plants/siegella-ornamental-plant-7523304_1920.jpg");
            productService.save(p3);

            Product p4 = new Product("Cactus Parodia", 15.50, "Cactus decorativo pequeño", List.of("Verde"));
            p4.setCategory(catPlants);
            addImage(p4, "plants/zenaga-cactus-7760970_1920.jpg");
            productService.save(p4);

            Product p5 = new Product("Planta Araña Variegada", 24.99, "Planta colgante de larga vida",
                    List.of("Blanco", "Verde"));
            p5.setCategory(catPlants);
            addImage(p5, "plants/martin_hetto-plant-5417765_1920.jpg");
            productService.save(p5);

            Product p6 = new Product("Ficus Lyrata", 55.00, "Higuera de hoja de violín", List.of("Verde oscuro"));
            p6.setCategory(catPlants);
            addImage(p6, "plants/datdotien0703-lotus-8057438_1920.jpg");
            productService.save(p6);

            Product p7 = new Product("Palma Areca", 38.75, "Palma tropical de interior", List.of("Verde"));
            p7.setCategory(catPlants);
            addImage(p7, "plants/robert102-flowers-194490_1920.jpg");
            productService.save(p7);

            Product p8 = new Product("Planta Serpiente", 28.00, "Sansevieria trifasciata",
                    List.of("Verde", "Amarillo"));
            p8.setCategory(catPlants);
            addImage(p8, "plants/schneeknirschen-flowers-174817_1920.jpg");
            productService.save(p8);

            Product p9 = new Product("Filodendro Rosa", 42.50, "Filodendro rosa de terciopelo",
                    List.of("Rosa", "Púrpura"));
            p9.setCategory(catPlants);
            addImage(p9, "plants/neelam279-flower-7965085_1920.jpg");
            productService.save(p9);

            Product p10 = new Product("Calatea Orbifolia", 35.99, "Planta decorativa elegante",
                    List.of("Verde", "Blanco"));
            p10.setCategory(catPlants);
            addImage(p10, "plants/lnitsyk-houseplant-7367379_1920.jpg");
            productService.save(p10);

            Product p11 = new Product("Begonia Maculada", 32.00, "Begonia de flores blancas",
                    List.of("Rojo", "Blanco"));
            p11.setCategory(catPlants);
            addImage(p11, "plants/ignartonosbg-green-leaves-7525661_1920.jpg");
            productService.save(p11);

            Product p12 = new Product("Peperomia Roja", 19.99, "Planta pequeña y decorativa", List.of("Rojo", "Verde"));
            p12.setCategory(catPlants);
            addImage(p12, "plants/siegella-ornamental-plant-7523304_1920.jpg");
            productService.save(p12);

            // ===== SOIL =====
            Product s1 = new Product("Tierra Universal Premium", 12.99, "Sustrato universal de calidad",
                    List.of("Marrón"));
            s1.setCategory(catSoil);
            addImage(s1, "soil/jing-soil-766281_1920.jpg");
            productService.save(s1);

            Product s2 = new Product("Sustrato para Cactus", 14.50, "Mezcla drenante especial",
                    List.of("Marrón claro"));
            s2.setCategory(catSoil);
            addImage(s2, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s2);

            Product s3 = new Product("Turba Rubia Natural", 11.75, "Turba sin abonos añadidos",
                    List.of("Marrón claro"));
            s3.setCategory(catSoil);
            addImage(s3, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(s3);

            Product s4 = new Product("Perlita de Sílice", 9.99, "Agente drenante ligero", List.of("Blanco"));
            s4.setCategory(catSoil);
            addImage(s4, "soil/61jQse8xSZL.jpg");
            productService.save(s4);

            Product s5 = new Product("Carbón Activado", 13.25, "Previene hongos y bacterias", List.of("Negro"));
            s5.setCategory(catSoil);
            addImage(s5,
                    "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(s5);

            Product s6 = new Product("Musgo Sphagnum Seco", 16.99, "Musgo de turba natural", List.of("Marrón claro"));
            s6.setCategory(catSoil);
            addImage(s6, "soil/jing-soil-766281_1920.jpg");
            productService.save(s6);

            Product s7 = new Product("Arena de Sílice Gruesa", 10.50, "Arena para mejora de drenaje",
                    List.of("Blanco"));
            s7.setCategory(catSoil);
            addImage(s7, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s7);

            Product s8 = new Product("Vermiculita Agrícola", 12.75, "Mantiene la humedad equilibrada",
                    List.of("Marrón claro"));
            s8.setCategory(catSoil);
            addImage(s8, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(s8);

            Product s9 = new Product("Zeolita Volcánica", 15.49, "Mejora la aireación del suelo",
                    List.of("Gris claro"));
            s9.setCategory(catSoil);
            addImage(s9, "soil/61jQse8xSZL.jpg");
            productService.save(s9);

            Product s10 = new Product("Corteza de Pino Molida", 11.25, "Sustrato para orquídeas", List.of("Marrón"));
            s10.setCategory(catSoil);
            addImage(s10,
                    "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(s10);

            Product s11 = new Product("Fibra de Coco Prensada", 13.99, "Sustrato ecológico versátil",
                    List.of("Marrón claro"));
            s11.setCategory(catSoil);
            addImage(s11, "soil/jing-soil-766281_1920.jpg");
            productService.save(s11);

            Product s12 = new Product("Mezcla Drenante Premium", 17.50, "Mix perfecto para suculentas",
                    List.of("Marrón claro"));
            s12.setCategory(catSoil);
            addImage(s12, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s12);

            // ===== TOOLS =====
            Product h1 = new Product("Pala de Trasplante Pequeña", 8.99, "Pala metálica para macetas",
                    List.of("Plateado"));
            h1.setCategory(catTools);
            addImage(h1, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(h1);

            Product h2 = new Product("Rastrillo de Mano", 9.75, "Rastrillo pequeño para suelo",
                    List.of("Plateado", "Negro"));
            h2.setCategory(catTools);
            addImage(h2, "tools/skdunning-rake-962756_1920.jpg");
            productService.save(h2);

            Product h3 = new Product("Podadera Manual", 16.50, "Tijeras de poda profesionales", List.of("Plateado"));
            h3.setCategory(catTools);
            addImage(h3, "tools/imageparty-secateurs-498632_1920.jpg");
            productService.save(h3);

            Product h4 = new Product("Regadera de 2 Litros", 14.25, "Regadera con rosa intercambiable",
                    List.of("Verde", "Plateado"));
            h4.setCategory(catTools);
            addImage(h4, "tools/katecox-watering-can-1466491_1920.jpg");
            productService.save(h4);

            Product h5 = new Product("Maceta Cerámica 30cm", 24.99, "Maceta decorativa con drenaje",
                    List.of("Blanco", "Gris"));
            h5.setCategory(catTools);
            addImage(h5, "tools/pexels-blur-1835403_1920.jpg");
            productService.save(h5);

            Product h6 = new Product("Maceta Plástica 20cm", 5.99, "Maceta económica con agujeros", List.of("Negro"));
            h6.setCategory(catTools);
            addImage(h6, "tools/4262159-gardening-4181074_1920.jpg");
            productService.save(h6);

            Product h7 = new Product("Soporte para Plantas", 19.50, "Estructura metálica para macetas",
                    List.of("Negro"));
            h7.setCategory(catTools);
            addImage(h7, "tools/elligraphix-garden-7429547_1920.jpg");
            productService.save(h7);

            Product h8 = new Product("Alambre de Jardín Enrollado", 7.75, "Alambre flexible de 50m", List.of("Verde"));
            h8.setCategory(catTools);
            addImage(h8, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(h8);

            Product h9 = new Product("Guantes de Jardín Premium", 12.50, "Guantes protectores de nitrilo",
                    List.of("Negro"));
            h9.setCategory(catTools);
            addImage(h9, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(h9);

            Product h10 = new Product("Pulverizador de 1.5L", 11.99, "Spray para riego y pesticidas",
                    List.of("Transparente"));
            h10.setCategory(catTools);
            addImage(h10, "tools/katecox-watering-can-1466491_1920.jpg");
            productService.save(h10);

            Product h11 = new Product("Tijeras de Poda Profesionales", 18.75, "Corte limpio y preciso",
                    List.of("Rojo"));
            h11.setCategory(catTools);
            addImage(h11, "tools/imageparty-secateurs-498632_1920.jpg");
            productService.save(h11);

            Product h12 = new Product("Cuerdas para Plantas", 6.50, "Cuerda de algodón para sostén", List.of("Marrón"));
            h12.setCategory(catTools);
            addImage(h12, "tools/skdunning-rake-962756_1920.jpg");
            productService.save(h12);

            // ===== CARE =====
            Product c1 = new Product("Fertilizante Universal Líquido", 13.99, "NPK 8-8-8 para todas las plantas",
                    List.of("Marrón"));
            c1.setCategory(catCare);
            addImage(c1,
                    "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(c1);

            Product c2 = new Product("Fungicida Ecológico", 15.50, "Previene y trata hongos naturalmente",
                    List.of("Amarillo"));
            c2.setCategory(catCare);
            addImage(c2, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(c2);

            Product c3 = new Product("Insecticida Natural de Neem", 16.99, "Control biológico de plagas",
                    List.of("Amarillo claro"));
            c3.setCategory(catCare);
            addImage(c3, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(c3);

            Product c4 = new Product("Vitaminas para Plantas", 17.25, "Estimulante de crecimiento", List.of("Azul"));
            c4.setCategory(catCare);
            addImage(c4, "soil/jing-soil-766281_1920.jpg");
            productService.save(c4);

            Product c5 = new Product("Enraizante Gel", 14.75, "Acelera el enraizamiento de esquejes",
                    List.of("Transparente"));
            c5.setCategory(catCare);
            addImage(c5, "soil/61jQse8xSZL.jpg");
            productService.save(c5);

            Product c6 = new Product("Repelente Anti-Plagas", 12.50, "Protección contra insectos", List.of("Blanco"));
            c6.setCategory(catCare);
            addImage(c6, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(c6);

            Product c7 = new Product("Regulador pH Down", 9.99, "Reduce el pH del sustrato", List.of("Transparente"));
            c7.setCategory(catCare);
            addImage(c7, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(c7);

            Product c8 = new Product("Extracto de Alga Marina", 18.50, "Estimulante natural de crecimiento",
                    List.of("Marrón"));
            c8.setCategory(catCare);
            addImage(c8, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(c8);

            Product c9 = new Product("Ácidos Húmicos Granulados", 11.99, "Mejora la estructura del suelo",
                    List.of("Negro"));
            c9.setCategory(catCare);
            addImage(c9,
                    "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(c9);

            Product c10 = new Product("Micorrizas en Polvo", 19.75, "Potencia la absorción de nutrientes",
                    List.of("Marrón claro"));
            c10.setCategory(catCare);
            addImage(c10, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(c10);

            Product c11 = new Product("Bacterias Benéficas Líquidas", 20.99, "Mejora la microbiología del sustrato",
                    List.of("Blanco lechoso"));
            c11.setCategory(catCare);
            addImage(c11, "soil/jing-soil-766281_1920.jpg");
            productService.save(c11);

            Product c12 = new Product("Aceite de Neem Concentrado", 21.50, "Tratamiento integral contra plagas",
                    List.of("Amarillo"));
            c12.setCategory(catCare);
            addImage(c12, "tools/4262159-gardening-4181074_1920.jpg");
            productService.save(c12);

            // Create users for reviews
            User u1 = new User("Agustin51", "Agustin", "agustin51@gmail.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(u1);
            User u2 = new User("Eduardo", "Eduardo", "eduardo@gmail.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(u2);
            User u3 = new User("Ampeterby7", "Ampeter", "ampeterby7@gmail.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(u3);
            User u4 = new User("XxConchaxX", "Concha", "concha@gmail.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(u4);
            User u5 = new User("Maria", "Maria", "maria@gmail.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(u5);

            // Add reviews to some products
            reviewService.save(new Review(p1, u1, "Monstera hermosa y robusta. Excelente calidad.", 4));
            reviewService.save(new Review(p1, u2, "Llego bien empaquetada, muy contento.", 5));
            reviewService.save(new Review(p2, u3, "Crece muy rápido, perfecta para trepar.", 3.5));
            reviewService.save(new Review(p2, u4, "Tolera la sombra, ideal para interiores.", 4.5));
            reviewService.save(new Review(p3, u5, "Suculenta hermosa y de fácil cuidado.", 3));

            System.out.println("Database initialized successfully. 48 products loaded in 4 categories.");
        }

        if (orderService.findAll().isEmpty()) {
            List<Product> products = productService.findAll();
            if (products.size() >= 2 && userNormal != null) {
                Order orderPrueba = new Order();
                orderPrueba.setUser(userNormal); // <--- LINKED TO "user"
                orderPrueba.addItem(new CartItem(products.get(0), 2));
                orderPrueba.addItem(new CartItem(products.get(1), 1));
                orderPrueba.setTotalPrice(orderPrueba.getTotalPrice());
                orderPrueba.setShippingCost(4.95);
                orderPrueba.setStatus(EnumStatus.DELIVERED); // So that they can write reviews
                orderService.save(orderPrueba);
            }

            // Create additional users for new orders
            User uLucas = new User("Lucas", "Lucas Garcia", "lucas@example.com", passwordEncoder.encode("pass"),
                    "USER");
            userRepository.save(uLucas);

            User uSofia = new User("Sofia", "Sofia Martin", "sofia@example.com", passwordEncoder.encode("pass"),
                    "USER");
            userRepository.save(uSofia);

            User uMateo = new User("Mateo", "Mateo Ruiz", "mateo@example.com", passwordEncoder.encode("pass"), "USER");
            userRepository.save(uMateo);

            List<Product> allProducts = productService.findAll();
            if (allProducts.size() >= 10) {
                Calendar cal = Calendar.getInstance();

                // Order 1: Lucas - February 2026
                cal.set(2026, Calendar.FEBRUARY, 10);
                Order o1 = new Order();
                o1.setUser(uLucas);
                o1.setOrderDate(cal.getTime());
                o1.addItem(new CartItem(allProducts.get(2), 1));
                o1.addItem(new CartItem(allProducts.get(5), 2));
                o1.setTotalPrice(o1.getTotalPrice());
                o1.setShippingCost(4.95);
                o1.setStatus(EnumStatus.DELIVERED);
                orderService.save(o1);

                // Order 2: Sofia - January 2026
                cal.set(2026, Calendar.JANUARY, 15);
                Order o2 = new Order();
                o2.setUser(uSofia);
                o2.setOrderDate(cal.getTime());
                o2.addItem(new CartItem(allProducts.get(10), 1));
                o2.setTotalPrice(o2.getTotalPrice());
                o2.setShippingCost(3.50);
                o2.setStatus(EnumStatus.DELIVERED);
                orderService.save(o2);

                // Order 3: Mateo - December 2025
                cal.set(2025, Calendar.DECEMBER, 5);
                Order o3 = new Order();
                o3.setUser(uMateo);
                o3.setOrderDate(cal.getTime());
                o3.addItem(new CartItem(allProducts.get(15), 3));
                o3.addItem(new CartItem(allProducts.get(20), 1));
                o3.setTotalPrice(o3.getTotalPrice());
                o3.setShippingCost(5.00);
                o3.setStatus(EnumStatus.DELIVERED);
                orderService.save(o3);

                // Order 4: Lucas - January 2026 (second order)
                cal.set(2026, Calendar.JANUARY, 28);
                Order o4 = new Order();
                o4.setUser(uLucas);
                o4.setOrderDate(cal.getTime());
                o4.addItem(new CartItem(allProducts.get(8), 2));
                o4.setTotalPrice(o4.getTotalPrice());
                o4.setShippingCost(4.95);
                o4.setStatus(EnumStatus.DELIVERED);
                orderService.save(o4);

                // Order 5: Sofia - February 2026
                cal.set(2026, Calendar.FEBRUARY, 20);
                Order o5 = new Order();
                o5.setUser(uSofia);
                o5.setOrderDate(cal.getTime());
                o5.addItem(new CartItem(allProducts.get(30), 1));
                o5.addItem(new CartItem(allProducts.get(31), 1));
                o5.setTotalPrice(o5.getTotalPrice());
                o5.setShippingCost(4.95);
                o5.setStatus(EnumStatus.PENDING);
                orderService.save(o5);
            }
        }
    }
}