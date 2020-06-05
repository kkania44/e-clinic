package pl.clinic.project.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
@SessionAttributes("user")
public class MvcLoginController {

    private UserService userService;

    public MvcLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    String loginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "login.html";
        } else {
            return "redirect:/";
        }
    }

}
