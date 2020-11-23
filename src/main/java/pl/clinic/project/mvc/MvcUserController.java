package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.model.NewPassword;
import pl.clinic.project.service.MailService;
import pl.clinic.project.exception.NotFoundException;
import pl.clinic.project.model.Email;
import pl.clinic.project.model.User;
import pl.clinic.project.password_generator.PasswordGenerator;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class MvcUserController {

    private MailService mailService;
    private UserService userService;

    public MvcUserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
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
            return "users/registerUser.html";
        }
        try {
            userService.registerUserAsPatient(user);
//        configure smtp client to send emails to users with confirmation
            mailService.sendMail(new Email(user.getEmail(), "Aktywacja konta", "Twoje konto w E-przychodni zostało aktywowane."));
            return "redirect:/login";
        } catch (AlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.user", e.getMessage());
            user.setEmail("");
            return "users/registerUser.html";
        }
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

    @GetMapping("/resetPassword")
    ModelAndView resetPasswordPage() {
        ModelAndView mav = new ModelAndView("users/resetPassword.html");
        String username = "";
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/resetPassword")
    String resetPassword(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        try {
            User userFromDB = userService.getByEmail(user.getEmail());
            String password = PasswordGenerator.generate();
            mailService.sendMail(new Email(userFromDB.getEmail(), "Reset hasła", "Twoje hasło w e-przychodni zostało zresetowane. \n" +
                    "Nowe hasło: " +password));

            userFromDB.setPassword(password);
            userService.setPassword(userFromDB);
        } catch (NotFoundException e) {
            bindingResult.rejectValue("email", "error.user", "Niepoprawny email");
            user.setEmail("");
            return "/users/resetPassword.html";
        }

        return "redirect:/login";
    }

    @GetMapping("/changePassword")
    @PreAuthorize("hasAnyRole('USER_PATIENT', 'USER_DOCTOR')")
    ModelAndView getChangingPasswordForm() {
        ModelAndView mav = new ModelAndView("users/changePassword.html");
        mav.addObject("newPassword", new NewPassword());
        return mav;
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasAnyRole('USER_PATIENT', 'USER_DOCTOR')")
    String changePassword(@Valid @ModelAttribute("newPassword") NewPassword newPassword, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/changePassword";
        }
        if (newPassword.arePasswordsSame()){
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getByEmail(username);
            user.setPassword(newPassword.getPassword1());
            userService.setPassword(user);
            return "redirect:/login";
        } else {
            bindingResult.rejectValue("password1", "error.newPassword", "Hasła nie są takie same");
            return "users/changePassword.html";
        }
    }

}
