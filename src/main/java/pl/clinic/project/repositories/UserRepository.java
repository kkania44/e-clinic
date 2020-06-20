package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.entities.UserEntity;
import pl.clinic.project.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByDoctor(DoctorEntity doctor);

    List<UserEntity> findAllByPatientNotNull();

    Optional<UserEntity> findByPatient(PatientEntity patientEntity);
}
