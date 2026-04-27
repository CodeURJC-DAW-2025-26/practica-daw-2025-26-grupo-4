package es.urjc.daw04.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    
    /**
     * Intercepta todas las rutas bajo /new que no terminen con una extensión de fichero
     * y las redirige hacia el Index principal descargado en el paso anterior.
     * De esta forma, el ruteo interno lo vuelve a asumir React Router en el cliente.
     */
    @GetMapping(value = {
        "/new", 
        "/new/", 
        "/new/admin",
        "/new/admin/products",
        "/new/product/{id}",
        "/new/cart",
        "/new/orders",
        "/new/recommendations",
        "/new/user",
        "/new/login",
        "/new/register"
    })
    public String redirectSpa() {
        return "forward:/new/index.html";
    }
}
