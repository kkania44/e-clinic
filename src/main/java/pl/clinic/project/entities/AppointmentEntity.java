package pl.clinic.project.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;

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
    @Column(name = "DR_ID", nullable = false)
    private Integer doctorId;
    @Column(name = "PT_ID", nullable = false)
    private Integer patientId;
    @Column(name = "AT_DATE", nullable = false, unique = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp date;

}
