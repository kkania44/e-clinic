package pl.clinic.project.mvc;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.User;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class MvcUserController {

    private UserService userService;
    @Autowired
    private ObjectFactory<HttpSession> httpSessionFactory;

    public MvcUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add")
    ModelAndView addUserPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("users/registerUser.html");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/add")
    String addNewUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "error.html";
        }
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ModelAndView getAllUsers() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("users/users.html");
        mav.addObject("users", userService.getAll());
        return mav;
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