package pl.clinic.project.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.clinic.project.UserRole;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "US_ID")
    private Integer id;

    @Column(name = "US_EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "US_PASSWORD",nullable = false)
    private String password;

    @Column(name = "US_ROLE")
    private UserRole role;

    @Column(name = "US_PATIENT_ID")
    private Integer patientId;

    @Column(name = "US_DOCTOR_ID")
    private Integer doctorId;

}
