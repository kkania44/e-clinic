package pl.clinic.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.clinic.project.UserRole;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.mapper.UserMapper;
import pl.clinic.project.model.User;
import pl.clinic.project.repositories.DoctorRepository;
import pl.clinic.project.repositories.PatientRepository;
import pl.clinic.project.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PatientRepository patientRepository = Mockito.mock(PatientRepository.class);
    private DoctorRepository doctorRepository = Mockito.mock(DoctorRepository.class);
    private PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UserMapper mapper = new UserMapper();
    private UserService service;

    @BeforeEach
    public void setup() {
        service = new UserService(userRepository, encoder, mapper, patientRepository, doctorRepository);
    }

    @Test
    public void shouldAddUserAsDoctor() {
        // given
        User user = getSampleUserDoctor();
        // when
        Mockito.when(doctorRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new DoctorEntity()));
        service.registerUserAsDoctor(user, 1);
        // then
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void shouldAddUserAsPatient() {
        // given
        User user = getSampleUserPatient();
        // when
        service.registerUserAsPatient(user);
        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
    }

    @Test
    public void shouldGetAllUsersBeingPatients() {
        // given
        UserEntity user1 = new UserEntity(1, "user2@wp.pl", "pass",
                                            UserRole.USER_PATIENT.getName(), null, null);
        UserEntity user2 = new UserEntity(2, "user3@wp.pl", "passwo",
                                            UserRole.USER_PATIENT.getName(), null, null);
        List<UserEntity> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        // when
        Mockito.when(userRepository.findAllByPatientNotNull()).thenReturn(users);
        // then
        assertEquals(2, users.size());
        assertEquals("user2@wp.pl", users.get(0).getEmail());
    }

    @Test
    public void shouldReturnUserIdWithMatchingDocId() {
        // given
        DoctorEntity doctor = new DoctorEntity(1, "doc1@wp.pl", "1242", UserRole.USER_DOCTOR.getName(), null, null);
        UserEntity user = new UserEntity(1, "user@wp.pl", "password", UserRole.USER_DOCTOR.getName(), null, null);
        // when
        Mockito.when(userRepository.findAllByDoctor(Mockito.any())).thenReturn(Collections.singletonList(user));
        Mockito.when(doctorRepository.findById(1)).thenReturn(Optional.of(doctor));
        Integer actualId = service.getUserIdByDoctorId(1);
        // then
        assertEquals(1, actualId);
    }

    @Test
    public void shouldUpdateUserWithRelatedPatient() {
        // given
        UserEntity userUpdate = new UserEntity(1, "user@wp.pl", "12412", null, null, null);
        PatientEntity patient = new PatientEntity(2, "Michal", "Rak", "80070055555", "123123123", null);
        // when
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(userUpdate));
        Mockito.when(patientRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(patient));
        service.setPatientId(getSampleUserPatient());
        // then
        Mockito.verify(userRepository).save(userUpdate);
    }


    private User getSampleUserDoctor() {
        return new User(1, "user@wp.pl", "password", UserRole.USER_DOCTOR, null, null);
    }

    private User getSampleUserPatient() {
        return new User(1, "user2@wp.pl", "pass", UserRole.USER_PATIENT, 1, null);
    }

}
