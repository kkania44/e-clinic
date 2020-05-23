package pl.clinic.project.entities.patients;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "patients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PT_ID", unique = true, nullable = false)
    private Integer id;
    @Column(name = "PT_FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "PT_LASTNAME", nullable = false)
    private String lastName;
    @Column(name = "PT_PESEL", nullable = false, length = 11, unique = true)
    private String peselNumber;
    @Column(name = "PT_PHONENUMBER", nullable = false, length = 9, unique = true)
    private Integer phoneNumber;


}
