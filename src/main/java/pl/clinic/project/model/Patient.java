package pl.clinic.project.model;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private Integer id;
    private String firstName;
    private String lastName;
    private String peselNumber;
    private String phoneNumber;
}
