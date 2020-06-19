package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.AppointmentEntity;
import pl.clinic.project.entities.DoctorEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Integer> {

    List<DoctorEntity> findAllByPhoneNumber(String phone);
}
