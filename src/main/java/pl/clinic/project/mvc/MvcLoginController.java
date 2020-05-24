package pl.clinic.project.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class MvcLoginController {

    @GetMapping
    String loginPage() {
        return "/login.html";
    }

}
