package pl.clinic.project.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AT_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DR_ID")
    private DoctorEntity doctor;

    @ManyToOne()
    @JoinColumn(name = "PT_ID")
    private PatientEntity patient;

    @Column(name = "AT_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "AT_TIME", nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

}
