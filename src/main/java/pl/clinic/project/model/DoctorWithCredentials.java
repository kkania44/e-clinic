package pl.clinic.project.model;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.clinic.project.validator.PhoneNumber;

@Component
@Scope(value = "request")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorWithCredentials {

    private Integer id;
    private String firstName;
    private String lastName;
    private String speciality;
    @PhoneNumber
    private String phoneNumber;

    private String login;
    private String password;
}
