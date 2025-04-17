package ua.hudyma.Theater2025.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.hudyma.Theater2025.repository.UserRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/error")
public class ErrorController {

//    UserRepository userRepository;

    @GetMapping
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        status = status != null ? status : "status not found";
        exception = exception != null ? exception : "exception not found";
        message = message != null ? message : "message not found";

        model.addAttribute("status", status);
        model.addAttribute("exception", exception);
        model.addAttribute("message", message);

        return "error"; // шаблон помилки
    }
}
