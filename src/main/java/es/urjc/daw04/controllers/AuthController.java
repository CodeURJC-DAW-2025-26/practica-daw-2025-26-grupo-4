package es.urjc.daw04.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import es.urjc.daw04.services.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, Model model) {
        if (userService.validateUser(username, password)) {
            return "redirect:/home";
        } else {
            model.addAttribute("loginError", true);
            return "login";
        }
    }

    @GetMapping("/user")
    public String user(Model model) {

        model.addAttribute("userName", "Edu");

        return "user";
    }
}
