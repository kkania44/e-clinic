package pl.clinic.project.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MvcMainPageController {

    @GetMapping
    String mainPage() {
        return "mainPage.html";
    }

}
