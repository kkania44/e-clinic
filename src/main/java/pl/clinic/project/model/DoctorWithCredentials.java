package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.clinic.project.validator.PhoneNumber;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
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
    @NotEmpty
    @Length(min = 2)
    private String firstName;
    @NotEmpty
    @Length(min = 2, max = 32)
    private String lastName;
    @Length(min = 2, max = 32)
    private String speciality;
    @PhoneNumber
    private String phoneNumber;

    @NotEmpty
    @Email
    private String login;
    @NotEmpty
    private String password;
}
