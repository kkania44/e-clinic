package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.clinic.project.validator.PhoneNumber;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @PESEL(message = "Nieprawidłowy numer PESEL")
    private String peselNumber;
    @PhoneNumber
    private String phoneNumber;
}
