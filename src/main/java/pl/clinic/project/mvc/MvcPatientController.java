package pl.clinic.project.mvc;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/patients")
@SessionAttributes("user")
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
    String addNewPatient (@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "error.html";
        }

        patientService.createPatient(patient);
        return "redirect:/patients/patientPanel";
    }

    @GetMapping("/patientPanel")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String patientPanelPage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        System.out.println(name);
        UserEntity user = userService.getByEmail(name).get();
        model.addAttribute("user", user);return "patients/patientPanel.html";
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

    @GetMapping("/{Id}")
    @PreAuthorize("isAuthenticated()")
        //TODO poprawić metodę żeby wyświetlała tylko dane aktualnie zalogowanego pacjenta
    ModelAndView showPatientData(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("#");
        mav.addObject("patient", patientService.getById((id)));
        return mav;
    }

}
