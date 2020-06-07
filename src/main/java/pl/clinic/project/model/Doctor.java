package pl.clinic.project.model;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.clinic.project.validator.PhoneNumber;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
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
