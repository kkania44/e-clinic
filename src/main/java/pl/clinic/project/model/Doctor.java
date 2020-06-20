package pl.clinic.project.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import pl.clinic.project.validator.PhoneNumber;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private Integer id;
    @NotNull
    @NotEmpty
    @Length(min = 2, max = 32)
    private String firstName;
    @NotNull
    @NotEmpty
    @Length(min = 2, max = 32)
    private String lastName;
    private String speciality;
    @PhoneNumber
    private String phoneNumber;
}
