package pl.clinic.project;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("ADMIN"), USER_DOCTOR("USER_DOCTOR"), USER_PATIENT("USER_PATIENT");

    UserRole(String name) {
        this.name = name;
    }

    private String name;

}
