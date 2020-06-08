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
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/patients")
@SessionAttributes({"user", "patient"})
public class MvcPatientController {

    private final PatientService patientService;
    private final UserService userService;

    public MvcPatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService=userService;
    }

    @GetMapping("/addPatient")
    @PreAuthorize("isAuthenticated()")
    ModelAndView addNewPatientPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patients/addPatient.html");
        mav.addObject("patient", new Patient());
        return mav;
    }

    @PostMapping("/addPatient")
    @PreAuthorize("isAuthenticated()")
    String addNewPatient (@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            final List<ObjectError> allErrors = bindingResult.getAllErrors();
            for(Object error: allErrors) {
                System.out.println(error.toString());
            }
            return "error.html";
        }
        final PatientEntity addedPatient = patientService.createPatient(patient);
        User user = (User) session.getAttribute("user");
        user.setPatientId(addedPatient.getId());
        userService.update(user);
        return "redirect:/patients/patientPanel";
    }

    @GetMapping("/patientPanel")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String patientPanelPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        model.addAttribute("user", user);
        if (user.getPatientId() != null) {
            return "patients/patientPanel.html";
        } else {
            return "redirect:/patients/addPatient";
        }
    }

    @GetMapping("/update")
    @PreAuthorize("hasRole('USER_PATIENT')")
    ModelAndView updatePatientPage(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patients/updatePatient.html");
        Patient patient = (Patient) session.getAttribute("patient");
        mav.addObject("patient", patient);

        return mav;
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String updatePatient(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "error.html";
        }

        patientService.updatePatient(patient);
        return "redirect:/patients/patientData";
    }

    @GetMapping("/patientData")
    @PreAuthorize("isAuthenticated()")
    ModelAndView showPatientData() { ;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patients/patientData.html");
        Patient patient = patientService.getById(id).get();
        mav.addObject("patient", patient);
        return mav;
    }

    @GetMapping("/deleteData")
    @PreAuthorize("isAuthenticated()")
    ModelAndView deletePatientAndUserData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getByEmail(email).get();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/logout");
        userService.deleteUser(user.getId());
        return mav;
    }

}
