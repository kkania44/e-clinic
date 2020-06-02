package pl.clinic.project.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/patients")
@SessionAttributes({"user", "patient"})
public class MvcPatientController {

    private final PatientService patientService;
    private final UserService userService;
    @Autowired
    private User user;

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
            model.addAttribute("errors", bindingResult.getAllErrors());
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

    @GetMapping("/updatePatient/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    ModelAndView updatePatientPage(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("#");

        Patient patient = patientService.getById(id).get();
        mav.addObject("patient", patient);

        return mav;
    }

    @PostMapping("/updatePatient")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String updatePatient(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "error.html";
        }

        patientService.updatePatient(patient);
        return "redirect:/mainPage";
    }

    @GetMapping("/patientData")
    @PreAuthorize("isAuthenticated()")
    ModelAndView showPatientData() { ;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("patients/patientData");
        mav.addObject("patient", patientService.getById(id));
        return mav;
    }

}
