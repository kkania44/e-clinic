package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.clinic.project.validator.PhoneNumber;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @PESEL
    private String peselNumber;
    @PhoneNumber
    private String phoneNumber;
}
