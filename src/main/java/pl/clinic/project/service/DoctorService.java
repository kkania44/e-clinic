package pl.clinic.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.clinic.project.entities.DoctorEntity;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.exception.NotFoundException;
import pl.clinic.project.mapper.DoctorMapper;
import pl.clinic.project.model.Doctor;
import pl.clinic.project.repositories.DoctorRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DoctorMapper mapper;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper mapper) {
        this.doctorRepository = doctorRepository;
        this.mapper = mapper;
    }

    public Integer createDoctor(Doctor doctor) {
        List<DoctorEntity> doctorsWithSamePhone = doctorRepository.findAllByPhoneNumber(doctor.getPhoneNumber());

        if (doctorsWithSamePhone.isEmpty()) {
            DoctorEntity doctorEntity = mapper.mapToEntity(doctor);
            return doctorRepository.save(doctorEntity).getId();
        } else {
            throw new AlreadyExistsException("Doktor z podanym numerem telefonu jest już w bazie.");
        }
    }

    @Transactional
    public void updateDoctor(Doctor doctor) {
        DoctorEntity doctorToUpdate = doctorRepository.findById(doctor.getId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono doktora."));
        doctorToUpdate.setLastName(doctor.getLastName());
        doctorToUpdate.setPhoneNumber(doctor.getPhoneNumber());
        doctorToUpdate.setSpeciality(doctor.getSpeciality());
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll().stream()
                .map(ent -> mapper.mapToApi(ent))
                .collect(Collectors.toList());
    }

    public Doctor getById(Integer id){
        return doctorRepository.findById(id)
                .map(ent -> mapper.mapToApi(ent))
                .orElseThrow(() -> new NotFoundException("Nie znaleziono doktora o id " +id));
    }
}
