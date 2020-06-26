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
import pl.clinic.project.pdf.GeneratePdf;
import pl.clinic.project.model.*;
import pl.clinic.project.service.AppointmentService;
import pl.clinic.project.service.DoctorService;
import pl.clinic.project.service.PatientService;
import pl.clinic.project.service.UserService;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<String> availableDates = AvailableDateTime.getWorkingDaysOfCurrentMonth(true);
        Doctor doctor = doctorService.getById(docId).get();

        Map<String, Object> mavObjects = Map.of("doctor", doctor,
                "dates", availableDates,
                "appointment", new Appointment());
        ModelAndView modelAndView = createModelAndViewWithMultiplyObjects(mavObjects);
        modelAndView.setViewName("appointments/bookAppointment.html");

        return modelAndView;
    }

    @PostMapping("book/time/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public ModelAndView chooseTimePage(@ModelAttribute("appointment") Appointment appointment,
                                       @PathVariable("id") Integer doctorId) {
        Doctor doctor = doctorService.getById(doctorId).get();
        List<LocalTime> occupiedHours = appointmentService.getAllHoursByDoctorIdAndDate(doctorId, appointment.getDate());
        List<String> hours = availableDateTime.getAvailableHours(occupiedHours);

        Map<String, Object> mavObjects = Map.of("doctor", doctor,
                "hours", hours,
                "appointment", appointment);
        ModelAndView modelAndView = createModelAndViewWithMultiplyObjects(mavObjects);
        modelAndView.setViewName("appointments/bookAppointmentHour.html");

        return modelAndView;
    }

    private static ModelAndView createModelAndViewWithMultiplyObjects(Map<String, Object> modelObjects) {
        ModelAndView mav = new ModelAndView();
        for (Map.Entry<String, Object> entry: modelObjects.entrySet()) {
            mav.addObject(entry.getKey(), entry.getValue());
        }
        return mav;
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasRole('USER_PATIENT')")
    public String createAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    @PathVariable("id") Integer docId, SessionStatus status) {
        String name = getUsername();
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
        ModelAndView mav = new ModelAndView("appointments/appointmentData.html");
        String name = getUsername();
        User user = userService.getByEmail(name).get();
        Integer id = user.getPatientId();
        List<Appointment> appointments = appointmentService.getAllByPatientId(id);
        List<AppointmentWithDoctorData> appointmentWithDoc = new ArrayList<>();
        boolean isAdmin = hasAdminRole();
        mav.addObject("isAdmin", isAdmin);

        for (Appointment appo: appointments) {
            appointmentWithDoc.add(createAppointmentWithDoctorData(appo));
        }
        mav.addObject("appointments", appointmentWithDoc);
        return mav;
    }

    private static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping(value = "/date/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('USER_DOCTOR', 'ADMIN')")
    ResponseEntity<InputStreamResource> showAppointmentsByDay(@PathVariable("id") Integer doctorId, @ModelAttribute("pickedDate") String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Appointment> appointments = appointmentService.getAllByDoctorIdAndDate(doctorId, localDate);
        List<AppointmentWithPatientData> appointmentsWithPt = new ArrayList<>();

        for (Appointment appo: appointments) {
            appointmentsWithPt.add(createAppointmentWithPatientData(appo));
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

    private AppointmentWithPatientData createAppointmentWithPatientData(Appointment appointment) {
        final Integer patientId = appointment.getPatientId();
        Patient patient = patientService.getById(patientId);
        AppointmentWithPatientData appointmentWithPt =
                new AppointmentWithPatientData(appointment.getId(), appointment.getTime(),
                        patient.getFirstName(), patient.getLastName());
        return appointmentWithPt;
    }

    private AppointmentWithDoctorData createAppointmentWithDoctorData(Appointment appointment) {
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
