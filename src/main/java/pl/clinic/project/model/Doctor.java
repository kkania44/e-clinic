package pl.clinic.project.model;

import lombok.*;
import pl.clinic.project.validator.PhoneNumber;

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
    @PhoneNumber
    private String phoneNumber;
}
