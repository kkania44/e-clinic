package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.clinic.project.validator.PhoneNumber;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    @NotEmpty
    private String lastName;
    @PESEL
    private String peselNumber;
    @PhoneNumber
    private String phoneNumber;
}
