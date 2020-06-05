package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@SessionAttributes({"user", "appointment"})
public class MvcAppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final PatientService patientService;

    public MvcAppointmentController(AppointmentService appointmentService, UserService userService, PatientService patientService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.patientService = patientService;
    }

    @GetMapping("/book")
    public ModelAndView createAppointmentPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("bookAppointment.html");
        mav.addObject("appointment", new Appointment());
        return mav;
    }

    @PostMapping("/book")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Integer patientId = user.getPatientId();
        appointment.setPatientId(patientId);
        appointmentService.createAppointment(appointment);
        return "redirect:/patients/patientPanel";
    }

    @GetMapping("/{id}")
    public ModelAndView showAppointment(@PathVariable("id") Integer id) {
        Appointment appointment = appointmentService.getById(id);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("appointmentData.html");
        mav.addObject("appointment", appointment);
        return mav;
    }


    @GetMapping("/appointmentData")
    @PreAuthorize("isAuthenticated()")
   ModelAndView showPatientData() { ;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        List<Appointment> appointments = appointmentService.getAll();
        for (Appointment appointment : appointments) {
            if (appointment.getPatientId().equals(id)) {
                mav.addObject("appointment", appointment);
                mav.setViewName("appointments/appointmentData.html");
            } else {
                mav.setViewName("error.html");
            }
        }
        return mav;
    }

}
