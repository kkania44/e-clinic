package pl.clinic.project.mvc;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.clinic.project.UserRole;
import java.util.Collection;

@Controller
@RequestMapping("/login")
public class MvcLoginController {

    @GetMapping
    String loginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "login/login.html";
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_"+ UserRole.ADMIN.getName()))) {
            return "redirect:/users/admin";
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_"+ UserRole.USER_PATIENT.getName()))) {
            return "redirect:/patients/patientPanel";
        } else {
            return "redirect:/doctors/panel";
        }
    }

}
