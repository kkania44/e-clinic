package pl.clinic.project.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DR_ID")
    private Integer id;
    @Column(name = "DR_FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "DR_LASTNAME", nullable = false)
    private String lastName;
    @Column(name = "DR_SPECIALITY", nullable = false)
    private String speciality;
    @Column(name = "DR_PHONENUMBER", nullable = false, unique = true, length = 9)
    private String phoneNumber;
}
