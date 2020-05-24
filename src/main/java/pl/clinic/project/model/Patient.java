package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.clinic.project.validator.PhoneNumber;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    private String firstName;
    private String lastName;

    @PESEL
    private String peselNumber;
    @PhoneNumber
    private String phoneNumber;
}
