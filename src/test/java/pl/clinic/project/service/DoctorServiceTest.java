package pl.clinic.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
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

    @Test
    public void shouldAddPatient() {
        // given
        Doctor doctor = new Doctor(null, "Michał", "Torba", "chirurg", "200200200");
        DoctorEntity doctorEntity = new DoctorEntity(
                1, "Michał", "Torba", "chirurg", "200200200", null);
        // when
        when(repository.findAllByPhoneNumber(doctor.getPhoneNumber())).thenReturn(Collections.emptyList());
        when(repository.save(Mockito.any(DoctorEntity.class))).thenReturn(doctorEntity);
        Integer actualId = service.createDoctor(doctor);
        // then
        assertEquals(1, actualId);
        verify(repository).save(Mockito.any(DoctorEntity.class));
    }

    @Test
    public void shouldNotAddDoctorWithSamePhone() {
        // given
        Doctor doctor = getSampleDoctor();
        // when
        when(repository.findAllByPhoneNumber(doctor.getPhoneNumber())).thenReturn(Collections.singletonList(new DoctorEntity()));
        Executable expectedEx = () -> service.createDoctor(doctor);
        // then
        assertThrows(AlreadyExistsException.class, expectedEx);
    }

    @Test
    public void shouldGetDoctorById() {
        // given
        DoctorEntity doctor = getSampleDoctorEntity();
        // when
        when(repository.findById(doctor.getId())).thenReturn(java.util.Optional.of(doctor));
        Doctor docById = service.getById(1);
        // then
        verify(repository).findById(Mockito.anyInt());
        assertEquals(doctor.getFirstName(), docById.getFirstName());
        assertEquals(doctor.getPhoneNumber(), docById.getPhoneNumber());
    }

    private DoctorEntity getSampleDoctorEntity() {
        return new DoctorEntity(1, "Michał", "Torba", "chirurg", "200200200", null);
    }

    private Doctor getSampleDoctor() {
        return new Doctor(1, "Michał", "Torba", "chirurg", "200200200");
    }

}
