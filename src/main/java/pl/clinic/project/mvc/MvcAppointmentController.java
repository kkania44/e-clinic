package pl.clinic.project.mvc;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.model.Appointment;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.model.User;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@SessionAttributes({"doctor", "appointment"})
public class MvcAppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private AvailableDateTime availableDateTime = new AvailableDateTime();
    private final UserService userService;

    public MvcAppointmentController(AppointmentService appointmentService, DoctorService doctorService, UserService userService) {
        this.appointmentService = appointmentService;
        this.doctorService=doctorService;
        this.userService=userService;
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public ModelAndView createAppointmentPage(@PathVariable("id") Integer docId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments/bookAppointment.html");
        List<String> availableDates = AvailableDateTime.getWorkingDaysOfCurrentMonth();
        Doctor doctor = doctorService.getById(docId).get();
        modelAndView.addObject("doctor", doctor);
        modelAndView.addObject("dates", availableDates);
        modelAndView.addObject("appointment", new Appointment());
        modelAndView.addObject("doctorId", docId);
        return modelAndView;
    }

    @PostMapping("book/time/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public ModelAndView chooseTimePage(@ModelAttribute("appointment") Appointment appointment,
                                       @PathVariable("id") Integer doctorId) {
        ModelAndView modelAndView = new ModelAndView();
        List<LocalTime> occupiedHours = appointmentService.getAllHoursByDoctorIdAndDate(doctorId, appointment.getDate());
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
                                    @PathVariable("id") Integer docId, SessionStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer patientId = user.getPatientId();
        appointment.setPatientId(patientId);
        appointment.setDoctorId(docId);
        appointmentService.createAppointment(appointment);
        status.setComplete();
        return "redirect:/patients/patientPanel";
    }

    @GetMapping("/appointmentData")
    @PreAuthorize("isAuthenticated()")
   ModelAndView showPatientData(HttpSession session) { ;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        List<Appointment> appointments = appointmentService.getAllByPatientId(id);
        List<Doctor> allDoctors = doctorService.getAll();
        List<Doctor> doctors = new ArrayList<>();

        for (Appointment appo: appointments) {
            for (Doctor doctor : allDoctors) {
                if (appo.getDoctorId().equals(doctor.getId())) {
                    doctors.add(doctor);
                }
            }
        }
        mav.addObject("appointments", appointments);
        mav.addObject("doctors", doctors);
        return mav;
    }

    @PostMapping("/date/{id}")
    @PreAuthorize("hasAnyRole('USER_DOCTOR', 'ADMIN')")
    ModelAndView showAppointmentsByDay(@PathVariable("id") Integer id, @ModelAttribute("pickedDate") String date) {
        ModelAndView mav = new ModelAndView("doctors/appointmentsByDate.html");
        LocalDate localDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.getAllByDoctorIdAndDate(id, localDate);
        Doctor doctor = doctorService.getById(id).get();
        mav.addObject("doctor", doctor);
        mav.addObject("date", date);
        mav.addObject("appointments", appointments);
        return mav;
    }

    @GetMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String deleteAppointment(@PathVariable("id") Integer id) {
        appointmentService.deleteById(id);
        return "redirect:/appointments/appointmentData";
    }

}
