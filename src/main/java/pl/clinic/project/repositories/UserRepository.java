package pl.clinic.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.clinic.project.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Integer> {

}
