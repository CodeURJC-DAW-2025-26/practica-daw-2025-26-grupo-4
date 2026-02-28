package es.urjc.daw04.controllers;

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
                message = "Solicitud incorrecta";
                break;
            case 403:
                message = "Acceso denegado";
                break;
            case 404:
                message = "Página no encontrada";
                break;
            case 405:
                message = "Método no permitido";
                break;
            case 500:
                message = "Error interno del servidor";
                break;
            default:
                HttpStatus httpStatus = HttpStatus.resolve(status);
                message = httpStatus != null ? httpStatus.getReasonPhrase() : "Error inesperado";
        }
        model.addAttribute("message", message);

        if (errorMessage != null && !errorMessage.isBlank()) {
            model.addAttribute("detail", errorMessage);
        } else {
            String defaultDetail;
            switch (status) {
                case 404:
                    defaultDetail = "El recurso que buscas no existe o ha sido eliminado.";
                    break;
                case 403:
                    defaultDetail = "No tienes permiso para acceder a esta página.";
                    break;
                case 500:
                    defaultDetail = "Ha ocurrido un problema en el servidor. Inténtalo más tarde.";
                    break;
                default:
                    defaultDetail = "Por favor, vuelve al inicio e inténtalo de nuevo.";
            }
            model.addAttribute("detail", defaultDetail);
        }

        model.addAttribute("is404", status == 404);
        model.addAttribute("is403", status == 403);
        model.addAttribute("is500", status == 500);

        return "errors";
    }
}
