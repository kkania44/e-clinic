package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.entities.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByDoctor(DoctorEntity doctor);
}
