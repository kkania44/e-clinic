package pl.clinic.project.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class MvcLoginController {

    private UserService userService;
    private ObjectFactory<HttpSession> httpSessionFactory;

    public MvcLoginController(UserService userService, ObjectFactory<HttpSession> httpSessionFactory) {
        this.userService = userService;
        this.httpSessionFactory = httpSessionFactory;
    }

    @GetMapping
    String loginPage() {
        return "login.html";
    }

    @PostMapping("/sessionData")
    String saveUserId(@ModelAttribute("username") String email) {
        UserEntity user = userService.getByEmail(email).get();
        HttpSession session = httpSessionFactory.getObject();
        session.setAttribute("userId", user.getId());
        return "redirect:/userId";
    }

}
