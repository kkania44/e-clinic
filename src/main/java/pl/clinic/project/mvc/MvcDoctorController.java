package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.model.*;
import pl.clinic.project.password_generator.PasswordGenerator;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.MailService;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class MvcDoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private MailService mailService;

    public MvcDoctorController(DoctorService doctorService, UserService userService, MailService mailService) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/addDoctor")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addNewDoctorPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin/addDoctor.html");
        mav.addObject("doctor", new DoctorWithCredentials());
        mav.addObject("isAdmin", true);
        return mav;
    }


    @PostMapping("/addDoctor")
    @PreAuthorize("hasRole('ADMIN')")
    String addNewDoctor(@Valid @ModelAttribute("doctor") DoctorWithCredentials doctor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/addDoctor.html";
        }
        Doctor doctorToAdd = new Doctor(null, doctor.getFirstName(), doctor.getLastName(),
                                        doctor.getSpeciality(), doctor.getPhoneNumber());
        Integer doctorId = doctorService.createDoctor(doctorToAdd);
        String pass = PasswordGenerator.generate();

        User user = User.builder()
                .email(doctor.getLogin())
                .password(pass)
                .build();

        userService.registerUserAsDoctor(user, doctorId);
        mailService.sendMail(new Email(user.getEmail(), "Hasło do konta", "Hasło do twojego konta w e-przychodni: " +pass));
        return "redirect:/users/admin";
    }

    @GetMapping("/panel")
    @PreAuthorize("hasRole('USER_DOCTOR')")
    ModelAndView showDoctorPanel() {
        ModelAndView mav = new ModelAndView("doctors/doctorPanel.html");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name);
        Integer doctorId = user.getDoctorId();
        String pickedDate = "";

        List<String> availableDates = AvailableDateTime.getWorkingDaysOfCurrentMonth(false);
        mav.addObject("doctorId", doctorId);
        mav.addObject("dates", availableDates);
        mav.addObject("pickedDate", pickedDate);
        return mav;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER_PATIENT', 'ADMIN')")
    ModelAndView showAllDoctors() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("listOfDoctors.html");
        List<Doctor> doctors = doctorService.getAll();
        boolean isAdmin = hasAdminRole();
        mav.addObject("doctors", doctors);
        mav.addObject("isAdmin", isAdmin);
        return mav;
    }

    @GetMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('USER_DOCTOR', 'ADMIN')")
    ModelAndView updateDoctorPage(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView("doctors/updateDoctor.html");
        Doctor doctorToUpdate = doctorService.getById(id);
        mav.addObject("doctor", doctorToUpdate);
        return mav;
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('USER_DOCTOR', 'ADMIN')")
    String updateDoctor(@Valid @ModelAttribute("doctor") Doctor doctor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "doctors/updateDoctor.html";
        }
        doctorService.updateDoctor(doctor);
        return "redirect:/doctors/panel";
    }

    @GetMapping("/changePassword")
    @PreAuthorize("hasRole('USER_DOCTOR')")
    ModelAndView getChangingPasswordForm() {
        ModelAndView mav = new ModelAndView("doctors/changePassword.html");
        mav.addObject("newPassword", new NewPassword());
        return mav;
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('USER_DOCTOR')")
    String changePassword(@Valid @ModelAttribute("newPassword") NewPassword newPassword, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "doctors/changePassword";
        }
        if (newPassword.arePasswordsSame()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getByEmail(username);
            user.setPassword(newPassword.getPassword1());
            userService.setPassword(user);
            return "redirect:/doctors/panel";
        } else {
            bindingResult.rejectValue("password1", "error.newPassword", "Hasła nie są takie same");
            return "doctors/changePassword.html";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteUserAndRelatedDoctor(@PathVariable("id") Integer id) {
        Integer userIdToDelete = userService
                .getUserByDoctorId(id)
                .getId();
        userService.deleteUser(userIdToDelete);
        return "redirect:/users/admin";
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
