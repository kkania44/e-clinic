package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.User;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class MvcUserController {

    private UserService userService;

    public MvcUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add")
    ModelAndView addUserPage() {
        ModelAndView mav = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            mav.setViewName("users/registerUser.html");
            mav.addObject("user", new User());
        } else {
            mav.setViewName("redirect:/login");
        }
        return mav;
    }

    @PostMapping("/add")
    String addNewUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "error.html";
        }
        userService.registerUserAsPatient(user);
        return "redirect:/login";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    String showAdminPanel() {
        return "/admin/adminPanel.html";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteUser(Integer id) {
        userService.deleteUser(id);
    }


}
