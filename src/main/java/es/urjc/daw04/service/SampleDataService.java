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
            Product p1 = new Product("Monstera Deliciosa", 45.99, "Planta de interior grande", List.of("Verde"));
            p1.setCategory(catPlantas);
            addImage(p1, "plants/lnitsyk-houseplant-7367379_1920.jpg");
            productService.save(p1);

            Product p2 = new Product("Pothos Dorado", 22.50, "Planta trepadora de fácil cuidado", List.of("Verde", "Amarillo"));
            p2.setCategory(catPlantas);
            addImage(p2, "plants/ignartonosbg-green-leaves-7525661_1920.jpg");
            productService.save(p2);

            Product p3 = new Product("Suculenta Jade", 18.99, "Pequeña planta carnuda", List.of("Verde"));
            p3.setCategory(catPlantas);
            addImage(p3, "plants/siegella-ornamental-plant-7523304_1920.jpg");
            productService.save(p3);

            Product p4 = new Product("Cactus Parodia", 15.50, "Cactus decorativo pequeño", List.of("Verde"));
            p4.setCategory(catPlantas);
            addImage(p4, "plants/zenaga-cactus-7760970_1920.jpg");
            productService.save(p4);

            Product p5 = new Product("Planta Araña Variegada", 24.99, "Planta colgante de larga vida", List.of("Blanco", "Verde"));
            p5.setCategory(catPlantas);
            addImage(p5, "plants/martin_hetto-plant-5417765_1920.jpg");
            productService.save(p5);

            Product p6 = new Product("Ficus Lyrata", 55.00, "Higuera de hoja de violín", List.of("Verde oscuro"));
            p6.setCategory(catPlantas);
            addImage(p6, "plants/datdotien0703-lotus-8057438_1920.jpg");
            productService.save(p6);

            Product p7 = new Product("Palma Areca", 38.75, "Palma tropical de interior", List.of("Verde"));
            p7.setCategory(catPlantas);
            addImage(p7, "plants/robert102-flowers-194490_1920.jpg");
            productService.save(p7);

            Product p8 = new Product("Planta Serpiente", 28.00, "Sansevieria trifasciata", List.of("Verde", "Amarillo"));
            p8.setCategory(catPlantas);
            addImage(p8, "plants/schneeknirschen-flowers-174817_1920.jpg");
            productService.save(p8);

            Product p9 = new Product("Filodendro Rosa", 42.50, "Filodendro rosa de terciopelo", List.of("Rosa", "Púrpura"));
            p9.setCategory(catPlantas);
            addImage(p9, "plants/neelam279-flower-7965085_1920.jpg");
            productService.save(p9);

            Product p10 = new Product("Calatea Orbifolia", 35.99, "Planta decorativa elegante", List.of("Verde", "Blanco"));
            p10.setCategory(catPlantas);
            addImage(p10, "plants/lnitsyk-houseplant-7367379_1920.jpg");
            productService.save(p10);

            Product p11 = new Product("Begonia Maculada", 32.00, "Begonia de flores blancas", List.of("Rojo", "Blanco"));
            p11.setCategory(catPlantas);
            addImage(p11, "plants/ignartonosbg-green-leaves-7525661_1920.jpg");
            productService.save(p11);

            Product p12 = new Product("Peperomia Roja", 19.99, "Planta pequeña y decorativa", List.of("Rojo", "Verde"));
            p12.setCategory(catPlantas);
            addImage(p12, "plants/siegella-ornamental-plant-7523304_1920.jpg");
            productService.save(p12);

            // ===== SUELO =====
            Product s1 = new Product("Tierra Universal Premium", 12.99, "Sustrato universal de calidad", List.of("Marrón"));
            s1.setCategory(catSuelo);
            addImage(s1, "soil/jing-soil-766281_1920.jpg");
            productService.save(s1);

            Product s2 = new Product("Sustrato para Cactus", 14.50, "Mezcla drenante especial", List.of("Marrón claro"));
            s2.setCategory(catSuelo);
            addImage(s2, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s2);

            Product s3 = new Product("Turba Rubia Natural", 11.75, "Turba sin abonos añadidos", List.of("Marrón claro"));
            s3.setCategory(catSuelo);
            addImage(s3, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(s3);

            Product s4 = new Product("Perlita de Sílice", 9.99, "Agente drenante ligero", List.of("Blanco"));
            s4.setCategory(catSuelo);
            addImage(s4, "soil/61jQse8xSZL.jpg");
            productService.save(s4);

            Product s5 = new Product("Carbón Activado", 13.25, "Previene hongos y bacterias", List.of("Negro"));
            s5.setCategory(catSuelo);
            addImage(s5, "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(s5);

            Product s6 = new Product("Musgo Sphagnum Seco", 16.99, "Musgo de turba natural", List.of("Marrón claro"));
            s6.setCategory(catSuelo);
            addImage(s6, "soil/jing-soil-766281_1920.jpg");
            productService.save(s6);

            Product s7 = new Product("Arena de Sílice Gruesa", 10.50, "Arena para mejora de drenaje", List.of("Blanco"));
            s7.setCategory(catSuelo);
            addImage(s7, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s7);

            Product s8 = new Product("Vermiculita Agrícola", 12.75, "Mantiene la humedad equilibrada", List.of("Marrón claro"));
            s8.setCategory(catSuelo);
            addImage(s8, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(s8);

            Product s9 = new Product("Zeolita Volcánica", 15.49, "Mejora la aireación del suelo", List.of("Gris claro"));
            s9.setCategory(catSuelo);
            addImage(s9, "soil/61jQse8xSZL.jpg");
            productService.save(s9);

            Product s10 = new Product("Corteza de Pino Molida", 11.25, "Sustrato para orquídeas", List.of("Marrón"));
            s10.setCategory(catSuelo);
            addImage(s10, "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(s10);

            Product s11 = new Product("Fibra de Coco Prensada", 13.99, "Sustrato ecológico versátil", List.of("Marrón claro"));
            s11.setCategory(catSuelo);
            addImage(s11, "soil/jing-soil-766281_1920.jpg");
            productService.save(s11);

            Product s12 = new Product("Mezcla Drenante Premium", 17.50, "Mix perfecto para suculentas", List.of("Marrón claro"));
            s12.setCategory(catSuelo);
            addImage(s12, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(s12);

            // ===== HERRAMIENTAS =====
            Product h1 = new Product("Pala de Trasplante Pequeña", 8.99, "Pala metálica para macetas", List.of("Plateado"));
            h1.setCategory(catHerramientas);
            addImage(h1, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(h1);

            Product h2 = new Product("Rastrillo de Mano", 9.75, "Rastrillo pequeño para suelo", List.of("Plateado", "Negro"));
            h2.setCategory(catHerramientas);
            addImage(h2, "tools/skdunning-rake-962756_1920.jpg");
            productService.save(h2);

            Product h3 = new Product("Podadera Manual", 16.50, "Tijeras de poda profesionales", List.of("Plateado"));
            h3.setCategory(catHerramientas);
            addImage(h3, "tools/imageparty-secateurs-498632_1920.jpg");
            productService.save(h3);

            Product h4 = new Product("Regadera de 2 Litros", 14.25, "Regadera con rosa intercambiable", List.of("Verde", "Plateado"));
            h4.setCategory(catHerramientas);
            addImage(h4, "tools/katecox-watering-can-1466491_1920.jpg");
            productService.save(h4);

            Product h5 = new Product("Maceta Cerámica 30cm", 24.99, "Maceta decorativa con drenaje", List.of("Blanco", "Gris"));
            h5.setCategory(catHerramientas);
            addImage(h5, "tools/pexels-blur-1835403_1920.jpg");
            productService.save(h5);

            Product h6 = new Product("Maceta Plástica 20cm", 5.99, "Maceta económica con agujeros", List.of("Negro"));
            h6.setCategory(catHerramientas);
            addImage(h6, "tools/4262159-gardening-4181074_1920.jpg");
            productService.save(h6);

            Product h7 = new Product("Soporte para Plantas", 19.50, "Estructura metálica para macetas", List.of("Negro"));
            h7.setCategory(catHerramientas);
            addImage(h7, "tools/elligraphix-garden-7429547_1920.jpg");
            productService.save(h7);

            Product h8 = new Product("Alambre de Jardín Enrollado", 7.75, "Alambre flexible de 50m", List.of("Verde"));
            h8.setCategory(catHerramientas);
            addImage(h8, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(h8);

            Product h9 = new Product("Guantes de Jardín Premium", 12.50, "Guantes protectores de nitrilo", List.of("Negro"));
            h9.setCategory(catHerramientas);
            addImage(h9, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(h9);

            Product h10 = new Product("Pulverizador de 1.5L", 11.99, "Spray para riego y pesticidas", List.of("Transparente"));
            h10.setCategory(catHerramientas);
            addImage(h10, "tools/katecox-watering-can-1466491_1920.jpg");
            productService.save(h10);

            Product h11 = new Product("Tijeras de Poda Profesionales", 18.75, "Corte limpio y preciso", List.of("Rojo"));
            h11.setCategory(catHerramientas);
            addImage(h11, "tools/imageparty-secateurs-498632_1920.jpg");
            productService.save(h11);

            Product h12 = new Product("Cuerdas para Plantas", 6.50, "Cuerda de algodón para sostén", List.of("Marrón"));
            h12.setCategory(catHerramientas);
            addImage(h12, "tools/skdunning-rake-962756_1920.jpg");
            productService.save(h12);

            // ===== CUIDADO =====
            Product c1 = new Product("Fertilizante Universal Líquido", 13.99, "NPK 8-8-8 para todas las plantas", List.of("Marrón"));
            c1.setCategory(catCuidado);
            addImage(c1, "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(c1);

            Product c2 = new Product("Fungicida Ecológico", 15.50, "Previene y trata hongos naturalmente", List.of("Amarillo"));
            c2.setCategory(catCuidado);
            addImage(c2, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(c2);

            Product c3 = new Product("Insecticida Natural de Neem", 16.99, "Control biológico de plagas", List.of("Amarillo claro"));
            c3.setCategory(catCuidado);
            addImage(c3, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(c3);

            Product c4 = new Product("Vitaminas para Plantas", 17.25, "Estimulante de crecimiento", List.of("Azul"));
            c4.setCategory(catCuidado);
            addImage(c4, "soil/jing-soil-766281_1920.jpg");
            productService.save(c4);

            Product c5 = new Product("Enraizante Gel", 14.75, "Acelera el enraizamiento de esquejes", List.of("Transparente"));
            c5.setCategory(catCuidado);
            addImage(c5, "soil/61jQse8xSZL.jpg");
            productService.save(c5);

            Product c6 = new Product("Repelente Anti-Plagas", 12.50, "Protección contra insectos", List.of("Blanco"));
            c6.setCategory(catCuidado);
            addImage(c6, "tools/sessions-photography-garden-1176406_1920.jpg");
            productService.save(c6);

            Product c7 = new Product("Regulador pH Down", 9.99, "Reduce el pH del sustrato", List.of("Transparente"));
            c7.setCategory(catCuidado);
            addImage(c7, "soil/stocksnap-dirty-2573292_1920.jpg");
            productService.save(c7);

            Product c8 = new Product("Extracto de Alga Marina", 18.50, "Estimulante natural de crecimiento", List.of("Marrón"));
            c8.setCategory(catCuidado);
            addImage(c8, "soil/tanuj_handa-gardening-2834728_1920.jpg");
            productService.save(c8);

            Product c9 = new Product("Ácidos Húmicos Granulados", 11.99, "Mejora la estructura del suelo", List.of("Negro"));
            c9.setCategory(catCuidado);
            addImage(c9, "soil/fertilizador-de-jardinería-y-trowel-una-bolsa-abono-los-derrames-en-tierra-rica-un-bosque-árboles-las-cercanías-listo-para-375922844.webp");
            productService.save(c9);

            Product c10 = new Product("Micorrizas en Polvo", 19.75, "Potencia la absorción de nutrientes", List.of("Marrón claro"));
            c10.setCategory(catCuidado);
            addImage(c10, "tools/alexas_fotos-pimples-1593916_1920.jpg");
            productService.save(c10);

            Product c11 = new Product("Bacterias Benéficas Líquidas", 20.99, "Mejora la microbiología del sustrato", List.of("Blanco lechoso"));
            c11.setCategory(catCuidado);
            addImage(c11, "soil/jing-soil-766281_1920.jpg");
            productService.save(c11);

            Product c12 = new Product("Aceite de Neem Concentrado", 21.50, "Tratamiento integral contra plagas", List.of("Amarillo"));
            c12.setCategory(catCuidado);
            addImage(c12, "tools/4262159-gardening-4181074_1920.jpg");
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