package es.urjc.daw04.controllers.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int status = statusCode != null ? statusCode : 500;
        model.addAttribute("status", status);

        String message;
        switch (status) {
            case 400:
                message = "Bad request";
                break;
            case 403:
                message = "Access denied";
                break;
            case 404:
                message = "Page not found";
                break;
            case 405:
                message = "Method not allowed";
                break;
            case 500:
                message = "Internal server error";
                break;
            default:
                HttpStatus httpStatus = HttpStatus.resolve(status);
                message = httpStatus != null ? httpStatus.getReasonPhrase() : "Unexpected error";
        }
        model.addAttribute("message", message);

        if (errorMessage != null && !errorMessage.isBlank()) {
            model.addAttribute("detail", errorMessage);
        } else {
            String defaultDetail;
            switch (status) {
                case 404:
                    defaultDetail = "The requested resource does not exist or has been removed.";
                    break;
                case 403:
                    defaultDetail = "You do not have permission to access this page.";
                    break;
                case 500:
                    defaultDetail = "A server error has occurred. Please try again later.";
                    break;
                default:
                    defaultDetail = "Please go back to the home page and try again.";
            }
            model.addAttribute("detail", defaultDetail);
        }

        model.addAttribute("is404", status == 404);
        model.addAttribute("is403", status == 403);
        model.addAttribute("is500", status == 500);

        return "errors";
    }
}
