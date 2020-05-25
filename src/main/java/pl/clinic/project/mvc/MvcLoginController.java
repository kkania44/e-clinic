package pl.clinic.project.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.clinic.project.model.User;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class MvcLoginController {

    @GetMapping
    String loginPage() {
        return "login.html";
    }

}
