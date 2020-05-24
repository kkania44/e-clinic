package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.pl.PESEL;

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
    private String phoneNumber;
}
