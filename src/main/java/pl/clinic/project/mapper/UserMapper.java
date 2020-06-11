package pl.clinic.project.mapper;


import pl.clinic.project.UserRole;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;

public class UserMapper {

    public User mapToApi(UserEntity userEntity) {
        if (userEntity.getPatient() != null) {
            return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), UserRole.valueOf(userEntity.getRole()),
                    userEntity.getPatient().getId(), null);
        }
        else if(userEntity.getDoctor() != null) {
            return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), UserRole.valueOf(userEntity.getRole()),
                    null, userEntity.getDoctor().getId());
        }
        else {
            return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), UserRole.valueOf(userEntity.getRole()),
                    null, null);
        }
    }

    public UserEntity mapToEntity(User user, PatientEntity patient, DoctorEntity doctor) {
        return new UserEntity(null, user.getEmail(), user.getPassword(), user.getRole().getName(), null, null);
    }

}
