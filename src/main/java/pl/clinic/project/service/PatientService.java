package pl.clinic.project.service;


import org.springframework.stereotype.Service;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.exception.AlreadyExistsException;
import pl.clinic.project.exception.NotFoundException;
import pl.clinic.project.mapper.PatientMapper;
import pl.clinic.project.model.Patient;
import pl.clinic.project.repositories.PatientRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private PatientRepository patientRepository;
    private PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = mapper;
    }

    public PatientEntity createPatient(Patient patient) {
        List<PatientEntity> patientsWithSamePhone = patientRepository.findAllByPhoneNumber(patient.getPhoneNumber());
        List<PatientEntity> patientsWithSamePesel = patientRepository.findAllByPeselNumber(patient.getPeselNumber());

        if (patientsWithSamePhone.size() != 0) {
            throw new AlreadyExistsException("Pacjent o podanym numerze telefonu jest już w bazie.");
        }
        if (patientsWithSamePesel.size() != 0) {
            throw new AlreadyExistsException("Pacjent o podanym numerze pesel jest już w bazie.");
        }

        PatientEntity patientToAdd = patientMapper.mapToEntity(patient);
        return patientRepository.save(patientToAdd);
    }

//    @Transactional
    public PatientEntity updatePatient(Patient patient) {
        PatientEntity patientToUpdate = patientRepository.findById(patient.getId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono pacjenta o id " + patient.getId()));
        patientToUpdate.setFirstName(patient.getFirstName());
        patientToUpdate.setLastName(patient.getLastName());
        patientToUpdate.setPhoneNumber(patient.getPhoneNumber());
        return patientRepository.save(patientToUpdate);
    }

    public Patient getById(Integer id) {
        return patientRepository.findById(id)
                .map(ent -> patientMapper.mapToApi(ent))
                .orElseThrow(() -> new NotFoundException("Nie znaleziono pacjenta"));
    }

    public List<Patient> getAll() {
        return patientRepository.findAll().stream()
                .map(ent -> patientMapper.mapToApi(ent))
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        patientRepository.deleteById(id);
    }
}
