package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.AppointmentEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {

    public List<AppointmentEntity> findAllByPatientId(Integer id);

    List<AppointmentEntity> findAllByDoctorIdAndDate(Integer id, LocalDate date);
}
