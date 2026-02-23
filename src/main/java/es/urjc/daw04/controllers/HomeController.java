package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "home";
    }
}