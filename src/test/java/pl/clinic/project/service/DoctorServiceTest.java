package pl.clinic.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.mapper.DoctorMapper;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.repositories.DoctorRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DoctorServiceTest {

    private DoctorRepository repository;
    private DoctorMapper mapper;
    private DoctorService service;

    @BeforeEach
    public void setup() {
        mapper = new DoctorMapper();
        repository = mock(DoctorRepository.class);
        service = new DoctorService(repository, mapper);
    }

    @Test
    public void shouldGetAllDoctors() {
        // given
        List<DoctorEntity> doctors = new ArrayList<>();
        doctors.add(getSampleDoctorEntity());
        doctors.add(new DoctorEntity(2, "Jolanta", "Worek", "rodzinny", "300300300", null));
        // when
        when(repository.findAll()).thenReturn(doctors);
        //then
        List<Doctor> actualDoctors = service.getAll();

        assertEquals(2, actualDoctors.size());
        assertEquals("Torba", actualDoctors.get(0).getLastName());
        assertEquals("Worek", actualDoctors.get(1).getLastName());
    }

//    @Test
//    public void shouldAddPatient() {
//        // given
//        Doctor doctor = new Doctor(1, "Michał", "Torba", "chirurg", "200200200");
//        // when
//        service.createDoctor(doctor);
//        // then
//        verify(repository).save(Mockito.any());
    }

    @Test
    public void shouldNotAddDoctorWithSamePhone() {
        // given
        Doctor doctor = new Doctor(1, "Michał", "Torba", "chirurg", "200200200");
        // when
        when(repository.findAllByPhoneNumber(doctor.getPhoneNumber())).thenReturn(Collections.singletonList(new DoctorEntity()));
        Executable expectedEx = () -> service.createDoctor(doctor);
        // then
        assertThrows(AlreadyExistsException.class, expectedEx);
    }

    private DoctorEntity getSampleDoctorEntity() {
        return new DoctorEntity(1, "Michał", "Torba", "chirurg", "200200200", null);
    }

}