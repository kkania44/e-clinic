package pl.clinic.project.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.User;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class MvcUserController {

    private UserService userService;

    @GetMapping("/add")
    ModelAndView addUserPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("users/addUser.html");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/add")
    String addNewUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    void deleteUser(Integer id) {
        userService.deleteUser(id);
    }


}
