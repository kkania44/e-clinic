package pl.clinic.project.entities;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "patients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PT_ID", unique = true)
    private Integer id;
    @Column(name = "PT_FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "PT_LASTNAME", nullable = false)
    private String lastName;
    @Column(name = "PT_PESEL", nullable = false, length = 11, unique = true)
    private String peselNumber;
    @Column(name = "PT_PHONENUMBER", nullable = false, unique = true)
    private String phoneNumber;

}
