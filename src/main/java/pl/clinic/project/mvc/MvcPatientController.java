package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.model.UserWithPatientData;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/patients")
@SessionAttributes({"user", "patient"})
public class MvcPatientController {

    private final PatientService patientService;
    private final UserService userService;

    public MvcPatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    ModelAndView addNewPatientPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.getPatientId() == null) {
            ModelAndView mav = new ModelAndView("patients/addPatient.html");
            mav.addObject("patient", new Patient());
            return mav;
        } else {
            return new ModelAndView("patients/patientPanel.html");
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    String addNewPatient (@Valid @ModelAttribute("patient") Patient patient,
                          BindingResult bindingResult,
                          HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "patients/addPatient.html";
        }
        final PatientEntity addedPatient = patientService.createPatient(patient);
        User user = (User) session.getAttribute("user");
        user.setPatientId(addedPatient.getId());
        userService.setPatientId(user);
        return "redirect:/patients/patientPanel";
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
            return "patients/updatePatient.html";
        }

        patientService.updatePatient(patient);
        return "redirect:/patients/patientData";
    }

    @GetMapping("/patientPanel")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String showPatientPanel(Model model) {
        String name = getUsername();
        User user = userService.getByEmail(name);
        model.addAttribute("user", user);
        if (user.getPatientId() != null) {
            Patient patient = patientService.getById(user.getPatientId());
            model.addAttribute("patient", patient);
            return "patients/patientPanel.html";
        } else {
            return "redirect:/patients";
        }
    }

    @GetMapping("/patientData")
    @PreAuthorize("isAuthenticated()")
    ModelAndView showPatientData() { ;
        ModelAndView mav = new ModelAndView("patients/patientData.html");
        String name = getUsername();
        User user = userService.getByEmail(name);
        Integer patientId = user.getPatientId();
        Patient patient = patientService.getById(patientId);
        mav.addObject("patient", patient);
        return mav;
    }

    @GetMapping("/deleteData")
    @PreAuthorize("isAuthenticated()")
    ModelAndView deletePatientAndUserData(HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/logout");
        String name = getUsername();
        User user = userService.getByEmail(name);
        userService.deleteUser(user.getId());
        return mav;
    }

    private static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    ModelAndView displayListOfPatients() {
        ModelAndView mav = new ModelAndView("admin/allUsersPatients.html");
        List<Patient> patients = patientService.getAll();
        List<UserWithPatientData> usersBeingPatients = createListOfUsersBeingPatients(patients);
        mav.addObject("users", usersBeingPatients);
        return mav;
    }

    private List<UserWithPatientData> createListOfUsersBeingPatients(List<Patient> patients) {
        List<UserWithPatientData> usersPatients = new ArrayList<>();
        for (Patient patient : patients) {
            User user = userService.getUserByPatientId(patient.getId());
            UserWithPatientData userPatient = new UserWithPatientData(
                    user.getId(), user.getEmail(),
                    patient.getId(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    patient.getPeselNumber(),
                    patient.getPhoneNumber()
            );
            usersPatients.add(userPatient);
        }
        return usersPatients;
    }
}
