package pl.clinic.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentWithDoctorData {
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private String dcFirstName;
    private String dcLastName;
    private String dcSpeciality;
    private String dcPhoneNumber;

}
