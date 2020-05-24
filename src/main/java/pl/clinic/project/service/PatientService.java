package pl.clinic.project.service;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import pl.clinic.project.entities.PatientEntity;
import pl.clinic.project.mapper.PatientMapper;
import pl.clinic.project.model.Patient;
import pl.clinic.project.repositories.PatientRepository;

import javax.transaction.Transactional;

@Service
public class PatientService {

    private PatientRepository patientRepository;
    private PatientMapper mapper;

    public PatientService(PatientRepository patientRepository, PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    public void createPatient(Patient patient) {
        PatientEntity patientToAdd = mapper.mapToEntity(patient);
        patientRepository.save(patientToAdd);
    }
    @Transactional
    public void updatePatient (Patient patient) throws NotFoundException {
        PatientEntity patientToUpdate = patientRepository.findById(patient.getId())
                .orElseThrow( () -> new NotFoundException(String.format("Nie znaleziono pacjenta o zadanym id")));

    }
}
