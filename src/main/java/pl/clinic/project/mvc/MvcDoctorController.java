package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.UserRole;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.model.DoctorWithCredentials;
import pl.clinic.project.model.User;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class MvcDoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    public MvcDoctorController(DoctorService doctorService, UserService userService) {
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping("/addDoctor")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView addNewDoctorPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("admin/addDoctor.html");
        mav.addObject("doctor", new DoctorWithCredentials());
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
        User user = User.builder()
                .email(doctor.getLogin())
                .password(doctor.getPassword())
                .build();
        userService.registerUserAsDoctor(user, doctorId);
        return "redirect:/users/admin";
    }

    @GetMapping("/panel")
    @PreAuthorize("hasRole('USER_DOCTOR')")
    ModelAndView showDoctorPanel(Model model) {
        ModelAndView mav = new ModelAndView("doctors/doctorPanel.html");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
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
        mav.addObject("doctors", doctors);
        boolean isAdmin = hasAdminRole();
        mav.addObject("isAdmin", isAdmin);
        return mav;
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


}
