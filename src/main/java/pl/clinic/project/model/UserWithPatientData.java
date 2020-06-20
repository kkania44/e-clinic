package pl.clinic.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserWithPatientData {

    private Integer userId;
    private String email;
    private Integer patientId;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phoneNumber;

}
