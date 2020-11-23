package pl.clinic.project.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.clinic.project.UserRole;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.exception.NotFoundException;
import pl.clinic.project.mapper.UserMapper;
import pl.clinic.project.model.Patient;
import pl.clinic.project.model.User;
import pl.clinic.project.repositories.DoctorRepository;
import pl.clinic.project.repositories.PatientRepository;
import pl.clinic.project.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public void registerUserAsDoctor(User user, Integer doctorId) {
        Optional<UserEntity> userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            throw new AlreadyExistsException("Użytkownik o podanym adresie email już istnieje");
        } else {
            UserEntity userEntity = UserEntity.builder()
                    .email(user.getEmail())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .role(UserRole.USER_DOCTOR.getName())
                    .doctor(doctorRepository.findById(doctorId).get())
                    .build();
            userRepository.save(userEntity);
        }
    }

    public void registerUserAsPatient(User user) {
        Optional<UserEntity> userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            throw new AlreadyExistsException("Użytkownik o podanym adresie email już istnieje");
        } else {
            UserEntity userEntity = UserEntity.builder()
                    .email(user.getEmail())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .role(UserRole.USER_PATIENT.getName())
                    .build();
            userRepository.save(userEntity);
        }
    }

    public List<User> getAllUsersPatients() {
        return userRepository.findAllByPatientNotNull().stream()
                .map(mapper::mapToApi)
                .collect(Collectors.toList());
    }

    public User getUserByPatientId(Integer id) {
        PatientEntity patient = patientRepository.findById(id).get();
        return userRepository.findByPatient(patient)
                .map(mapper::mapToApi)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono usera"));
    }

    public User getUserByDoctorId(Integer id) {
        DoctorEntity doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono doktora"));
        return userRepository.findByDoctor(doctor)
                .map(mapper::mapToApi)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono usera."));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(mapper::mapToApi)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika " +email));
    }

    public void setPassword(User user){
        UserEntity userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika " +user.getEmail()));
        userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userToUpdate);
    }

    public void setPatientId(User user) {
        UserEntity userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono zalogowanego użytkownika "));
        userToUpdate.setPatient(patientRepository.findById(user.getPatientId()).get());
        userRepository.save(userToUpdate);
    }

    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }


}
