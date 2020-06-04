package pl.clinic.project.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.service.AppointmentService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/appointments")
@SessionAttributes({"patient"})
public class MvcAppointmentController {

    private final AppointmentService appointmentService;

    public MvcAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
        appointmentService.createAppointment(appointment);
        return "redirect:/patients/patientPanel";
    }

}
