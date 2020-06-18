package pl.clinic.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentWithPatientData {

    private Integer id;
    private LocalTime time;
    private String ptFirstName;
    private String ptLastName;

}
