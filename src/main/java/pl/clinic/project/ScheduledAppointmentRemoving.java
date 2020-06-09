package pl.clinic.project;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.clinic.project.service.AppointmentService;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class ScheduledAppointmentRemoving {

    private static final Logger log = Logger.getLogger(ScheduledAppointmentRemoving.class.getName());
    private AppointmentService appointmentService;

    public ScheduledAppointmentRemoving(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void removePastAppointments() {
        LocalDate currentDate = LocalDate.now();
        appointmentService.deleteAppointmentsWithPastDate(currentDate);
        log.info("Usunięto stare wizyty");
    }
}
