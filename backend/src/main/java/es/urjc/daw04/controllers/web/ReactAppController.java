package es.urjc.daw04.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/new")
public class ReactAppController {

    /**
     * Forward SPA routes (without file extension) to React entrypoint.
     * Static assets under /new/** are served directly by Spring static resource handling.
     */
    @GetMapping({ "", "/" })
    public String forwardToIndex() {
        return "forward:/new/index.html";
    }
}


