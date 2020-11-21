package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.clinic.project.validator.PhoneNumber;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Component
@Scope(value = "request")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorWithCredentials {

    private Integer id;
    @NotEmpty(message = "Pole nie może być puste")
    @Length(min = 2)
    private String firstName;
    @NotEmpty(message = "Pole nie może być puste")
    @Length(min = 2, max = 32, message = "Pole musi zawierać przynajmniej 2 znaki")
    private String lastName;
    @Length(min = 2, max = 32, message = "Pole musi zawierać przynajmniej 2 znaki")
    private String speciality;
    @PhoneNumber
    private String phoneNumber;

    @NotEmpty(message = "Pole nie może być puste")
    @Email(message = "Nieprawidłowy format adresu e-mail")
    private String login;

}
