package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.clinic.project.entities.PatientEntity;

public interface PatientRepository extends JpaRepository <PatientEntity, Integer> {
}
