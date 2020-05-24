package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.PatientEntity;

@Repository
public interface PatientRepository extends JpaRepository <PatientEntity, Integer> {
}
