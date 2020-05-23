package pl.clinic.project.model;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private Integer id;
    private String firstName;
    private String lastName;
    private String speciality;
    private String phoneNumber;
}
