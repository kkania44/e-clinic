package pl.clinic.project.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class MvcLoginController {

    private UserService userService;

    public MvcLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    String loginPage() {
        return "login.html";
    }

    @RequestMapping("/sessionData")
    public String saveToSession(@ModelAttribute("username") String email, HttpSession session) {
        UserEntity user = userService.getByEmail(email).get();
        session.setAttribute("user", user);
        return "/patients/patientPanel";
    }

}
