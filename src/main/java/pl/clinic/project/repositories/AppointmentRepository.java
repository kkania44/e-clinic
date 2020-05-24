package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.clinic.project.entities.AppointmentEntity;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {


}
