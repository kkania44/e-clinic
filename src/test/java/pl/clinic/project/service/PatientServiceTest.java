package pl.clinic.project.service;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.mapper.PatientMapper;
import pl.clinic.project.model.Patient;
import pl.clinic.project.repositories.PatientRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PatientServiceTest {

    private PatientService service;
    private PatientRepository repository;
    private PatientMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new PatientMapper();
        repository = Mockito.mock(PatientRepository.class);
        service = new PatientService(repository, mapper);
    }

    @Test
    public void shouldAddPatient() {
        // given
        Patient patient = new Patient(null, "Krzysztof", "Krawczyk",
                "10010055555", "200200200");
        // when
        service.createPatient(patient);
        // then
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    public void shouldNotAddPatientWithExistingPeselNumber() {
        // given
        Patient patient = new Patient(null, "Krzysztof", "Krawczyk",
                "10010055555", "200200200");
        // when
        Mockito.when(repository.findAllByPeselNumber(patient.getPeselNumber()))
                .thenReturn(Collections.singletonList(new PatientEntity()));
        Executable expectedException = () -> service.createPatient(patient);
        // then
        assertThrows(AlreadyExistsException.class, expectedException);
    }

//    @Test
//    public void shouldUpdatePatient() {
//        // given
//        Patient patient = new Patient(1, "Krzysztof", "Krawczyk",
//                "10010055555", "200200200");
//        PatientEntity patientFromDB = new PatientEntity();
//        // when
//        Mockito.when(repository.findById(patient.getId())).thenReturn(Optional.of(patientFromDB));
//        PatientEntity savedPatient = service.updatePatient(patient);
//        // then
//        assertEquals(patient.getPhoneNumber(), savedPatient.getPhoneNumber());
//    }

    @Test
    public void shouldGetPatientById() {
        // given
        PatientEntity patientfromDB = new PatientEntity(1, "Krzysztof", "Krawczyk",
                "10010055555", "200200200", null);
        // when
        Mockito.when(repository.findById(patientfromDB.getId())).thenReturn(Optional.of(patientfromDB));
        Patient actualPatient = service.getById(1);
        // then
        assertEquals(patientfromDB.getLastName(), actualPatient.getLastName());
        assertEquals(patientfromDB.getPhoneNumber(), actualPatient.getPhoneNumber());
    }

}