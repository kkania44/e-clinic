package pl.clinic.project.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.mapper.UserMapper;
import pl.clinic.project.model.User;
import pl.clinic.project.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public void registerUser(User user) {
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role("USER_PATIENT")
                .patient(null)
                .doctor(null)
                .build();
        userRepository.save(userEntity);
    }

    public List<User> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::mapToApi)
                .collect(Collectors.toList());
    }

    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }


}
