package pl.clinic.project.model;

import lombok.*;
import pl.clinic.project.validator.PeselNumberValidator;
import pl.clinic.project.validator.ValidPeselNumber;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    private String firstName;
    private String lastName;
    private String peselNumber;
    private String phoneNumber;
}
