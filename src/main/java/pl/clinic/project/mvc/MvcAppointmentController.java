package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.model.User;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;

import javax.servlet.http.HttpSession;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@SessionAttributes({"user", "appointment"})
public class MvcAppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private AvailableDateTime availableDateTime = new AvailableDateTime();

    public MvcAppointmentController(AppointmentService appointmentService, DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.doctorService=doctorService;
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public ModelAndView createAppointmentPage(@PathVariable("id") Integer docId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments/bookAppointment.html");
        List<String> availableDates = AvailableDateTime.getWorkingDaysOfCurrentMonth();

        modelAndView.addObject("dates", availableDates);
        modelAndView.addObject("appointment", new Appointment());
        modelAndView.addObject("doctorId", docId);
        return modelAndView;
    }

    @PostMapping("book/time/{id}")
    public ModelAndView chooseTimePage(@ModelAttribute("appointment") Appointment appointment,
                                       @PathVariable("id") Integer doctorId) {
        ModelAndView modelAndView = new ModelAndView();
        List<LocalTime> occupiedHours = appointmentService.getAllByDoctorIdAndDate(doctorId, appointment.getDate());
        List<String> hours = availableDateTime.getAvailableHours(occupiedHours);

        modelAndView.addObject("appointment", appointment);
        modelAndView.addObject("hours", hours);
        modelAndView.addObject("doctorId", doctorId);
        modelAndView.setViewName("appointments/bookAppointmentHour.html");
        return modelAndView;
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    @PathVariable("id") Integer docId, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Integer patientId = user.getPatientId();

        appointment.setPatientId(patientId);
        appointment.setDoctorId(docId);
        appointmentService.createAppointment(appointment);
        return "redirect:/patients/patientPanel";
    }

    @GetMapping("/appointmentData")
    @PreAuthorize("isAuthenticated()")
   ModelAndView showPatientData(HttpSession session) { ;
        User user = (User)session.getAttribute("user");
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        List<Appointment> appointments = appointmentService.getAllByPatientId(id);
        List<Doctor> allDoctors = doctorService.getAll();
        List<Doctor> doctors = new ArrayList<>();

        for (int i = 0; i<allDoctors.size(); i++){
            for (int y = 0; y<appointments.size(); y++){
                if (allDoctors.get(i).getId().equals(appointments.get(y).getDoctorId())){
                    doctors.add(allDoctors.get(i));
                }
            }
        }
        mav.addObject("appointments", appointments);
        mav.addObject("doctors", doctors);
        return mav;
    }

    @GetMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String deleteAppointment(@PathVariable("id") Integer id) {
        appointmentService.deleteById(id);
        return "redirect:/appointments/appointmentData";
    }

}
