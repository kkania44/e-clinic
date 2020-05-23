package pl.clinic.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    private Integer id;
    private Integer doctorId;
    private Integer patientId;
    private LocalDate date;
    private LocalTime time;

}
