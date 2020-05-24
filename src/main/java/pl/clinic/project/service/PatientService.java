package pl.clinic.project.service;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import pl.clinic.project.entities.PatientEntity;
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

    public void createPatient(Patient patient) {
        PatientEntity patientToAdd = patientMapper.mapToEntity(patient);
        patientRepository.save(patientToAdd);
    }

    @Transactional
    public void updatePatient (Patient patient) throws NotFoundException {
        PatientEntity patientToUpdate = patientRepository.findById(patient.getId())
                .orElseThrow( () -> new NotFoundException(String.format("Nie znaleziono pacjenta o id " + patient.getId())));
    patientToUpdate.setPhoneNumber(patient.getPhoneNumber());
    }

    public Optional<Patient> getById(Integer id) {
        return patientRepository.findById(id)
                .map(ent -> patientMapper.mapToApi(ent));
    }

    public List<Patient> getAll() {
        return patientRepository.findAll().stream()
                .map(ent -> patientMapper.mapToApi(ent))
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {patientRepository.deleteById(id);}
}
