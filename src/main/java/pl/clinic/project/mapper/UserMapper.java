package pl.clinic.project.mapper;

import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.User;

public class UserMapper {

    public User mapToApi(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), userEntity.getRole());
    }

    public UserEntity mapToEntity(User user) {
        return new UserEntity(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }

}
