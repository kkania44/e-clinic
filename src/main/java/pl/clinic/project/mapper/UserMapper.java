package pl.clinic.project.mapper;


import pl.clinic.project.UserRole;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;

public class UserMapper {

    public User mapToApi(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), UserRole.valueOf(userEntity.getRole()),
                userEntity.getPatient().getId(), userEntity.getDoctor().getId());
    }

    public UserEntity mapToEntity(User user, PatientEntity patient, DoctorEntity doctor) {
        return new UserEntity(null, user.getEmail(), user.getPassword(), user.getRole().getName(), null, null);
    }

}
