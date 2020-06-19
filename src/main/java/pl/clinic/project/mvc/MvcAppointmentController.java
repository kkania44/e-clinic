package pl.clinic.project.mvc;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import pl.clinic.project.AvailableDateTime;
import pl.clinic.project.GeneratePdf;
import pl.clinic.project.model.*;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private final PatientService patientService;
    private AvailableDateTime availableDateTime = new AvailableDateTime();
    private final UserService userService;

    public MvcAppointmentController(AppointmentService appointmentService, DoctorService doctorService,
                                    PatientService patientService, UserService userService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public ModelAndView createAppointmentPage(@PathVariable("id") Integer docId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments/bookAppointment.html");
        List<String> availableDates = AvailableDateTime.getWorkingDaysOfCurrentMonth(true);
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
        Doctor doctor = doctorService.getById(doctorId).get();
        List<LocalTime> occupiedHours = appointmentService.getAllHoursByDoctorIdAndDate(doctorId, appointment.getDate());
        List<String> hours = availableDateTime.getAvailableHours(occupiedHours);
        modelAndView.addObject("appointment", appointment);
        modelAndView.addObject("hours", hours);
        modelAndView.addObject("doctorId", doctorId);
        modelAndView.addObject("doctor", doctor);
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
    @PreAuthorize("hasAnyRole('USER_PATIENT', 'ADMIN')")
   ModelAndView showPatientData() { ;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        ModelAndView mav = new ModelAndView();
        List<Appointment> appointments = appointmentService.getAllByPatientId(id);

        List<AppointmentWithDoctorData> appointmentWithDoc = new ArrayList<>();
        boolean isAdmin = hasAdminRole();
        mav.addObject("isAdmin", isAdmin);

        for (Appointment appo: appointments) {
            appointmentWithDoc.add(createAppointmentWithDoc(appo));
        }
        mav.addObject("appointments", appointmentWithDoc);
        return mav;
    }

    @GetMapping(value = "/date/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('USER_DOCTOR', 'ADMIN')")
    ResponseEntity<InputStreamResource> showAppointmentsByDay(@PathVariable("id") Integer doctorId, @ModelAttribute("pickedDate") String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.getAllByDoctorIdAndDate(doctorId, localDate);
        List<AppointmentWithPatientData> appointmentsWithPt = new ArrayList<>();

        for (Appointment appo: appointments) {
            appointmentsWithPt.add(createAppointmentWithPt(appo));
        }

        ByteArrayInputStream inputStream = GeneratePdf.appointmentsList(appointmentsWithPt, date);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=listaWizyt.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    @GetMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    String deleteAppointment(@PathVariable("id") Integer id) {
        appointmentService.deleteById(id);
        return "redirect:/appointments/appointmentData";
    }

    private AppointmentWithPatientData createAppointmentWithPt(Appointment appointment) {
        final Integer patientId = appointment.getPatientId();
        Patient patient = patientService.getById(patientId);
        AppointmentWithPatientData appointmentWithPt =
                new AppointmentWithPatientData(appointment.getId(), appointment.getTime(),
                        patient.getFirstName(), patient.getLastName());
        return appointmentWithPt;
    }

    private AppointmentWithDoctorData createAppointmentWithDoc(Appointment appointment) {
        final Integer doctorId = appointment.getDoctorId();
        Doctor doctor = doctorService.getById(doctorId).get();
        return new AppointmentWithDoctorData(
                appointment.getId(),
                appointment.getDate(),
                appointment.getTime(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpeciality(),
                doctor.getPhoneNumber());
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }


}
