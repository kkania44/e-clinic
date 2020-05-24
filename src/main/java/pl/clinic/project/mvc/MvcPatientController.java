package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.Patient;
import pl.clinic.project.service.PatientService;

import javax.validation.Valid;

@Controller
@RequestMapping("/patients")
public class MvcPatientController {

    private final PatientService patientService;

    public MvcPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/addPatient")
    ModelAndView addNewPatientPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("#");
        mav.addObject("patient", new Patient());
        return mav;
    }

    @PostMapping("/addPatient")
    String addNewPatient (@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "error.html";
        }

        patientService.createPatient(patient);
        return "redirect:/login";
    }

    @GetMapping("/updatePatient/{id}")
    ModelAndView updatePatientPage(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("#");

        Patient patient = patientService.getById(id).get();
        mav.addObject("patient", patient);

        return mav;
    }

    @PostMapping("/updatePatient")

}
