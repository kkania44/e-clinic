package pl.clinic.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewPassword {

    @NotNull
    @Size(min = 6, message = "Hasło musi zawierać przynajmniej 6 znaków")
    private String password1;
    @NotNull
    @Size(min = 6, message = "Hasło musi zawierać przynajmniej 6 znaków")
    private String password2;

    public boolean arePasswordsSame() {
        return password1.equals(password2);
    }
}
