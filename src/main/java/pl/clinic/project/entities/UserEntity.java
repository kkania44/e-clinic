package pl.clinic.project.entities;

import lombok.*;
import pl.clinic.project.UserRole;
import pl.clinic.project.model.Patient;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
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
    private String role;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name="PT_ID")
    private PatientEntity patient;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "DR_ID")
    private DoctorEntity doctor;

}
