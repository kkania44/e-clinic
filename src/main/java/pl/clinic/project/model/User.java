package pl.clinic.project.model;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.clinic.project.UserRole;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;
    @Email
    @NotBlank
    private String email;
    private String password;
    private UserRole role;
    private Integer patientId;
    private Integer doctorId;

}
