package pl.clinic.project.mvc;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.model.DoctorWithCredentials;
import pl.clinic.project.model.User;
import pl.clinic.project.password_generator.PasswordGenerator;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/doctors")
public class MvcDoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private JavaMailSender mailSender;

    public MvcDoctorController(DoctorService doctorService, UserService userService, JavaMailSender mailSender) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.mailSender = mailSender;
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
        Logger.getLogger(MvcDoctorController.class.getName()).log(Level.INFO, pass);
//        configure smtp client to send emails with password to doctors
//        sendSimpleMail(user.getEmail(), "Hasło do konta", "Hasło do twojego konta w e-przychodni: "+pass);
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

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    String deleteUserAndRelatedDoctor(@PathVariable("id") Integer id) {
        Integer userIdToDelete = userService.getUserIdByDoctorId(id);
        userService.deleteUser(userIdToDelete);
        return "redirect:/users/admin";
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void sendSimpleMail(String to, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
